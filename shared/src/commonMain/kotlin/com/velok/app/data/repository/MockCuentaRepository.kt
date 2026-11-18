package com.velok.app.data.repository

import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.Usuario
import com.velok.app.domain.repository.AuthRepository
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

/**
 * Cuentas en memoria mientras no hay backend. Implementa autenticación y usuario actual en la
 * misma clase porque comparten los datos (quién inició sesión).
 *
 * Solo para pruebas: guarda las contraseñas tal cual. Un backend real las guardaría cifradas
 * (con hash) y nunca las enviaría de vuelta a la app.
 */
class MockCuentaRepository : AuthRepository, UsuarioRepository {

    private class Cuenta(val usuario: Usuario, val contrasena: String)

    private val cuentas = mutableMapOf(
        CORREO_DEMO to Cuenta(
            usuario = Usuario(
                id = "usuario-demo",
                nombre = "Checo",
                correo = CORREO_DEMO,
                perfil = PerfilFisico(
                    fechaNacimiento = LocalDate(2004, 4, 25),
                    unidadPeso = UnidadPeso.KG,
                    estaturaCm = 170,
                    pesoKg = 100.0,
                    meta = MetaEntrenamiento.PERDER_GRASA,
                ),
            ),
            contrasena = CONTRASENA_DEMO,
        ),
    )

    private val actual = MutableStateFlow<Usuario?>(null)
    private var siguienteId = 1

    override fun observarUsuarioActual(): Flow<Usuario?> = actual

    override suspend fun ingresar(correo: String, contrasena: String): ResultadoAuth {
        delay(RETRASO_SIMULADO_MS)
        val cuenta = cuentas[correo]
        if (cuenta == null || cuenta.contrasena != contrasena) {
            // Mismo mensaje para correo inexistente y contraseña incorrecta: no revela qué cuentas existen.
            return ResultadoAuth.Fallo(ErrorCuenta.CREDENCIALES_INCORRECTAS)
        }
        return iniciarSesion(cuenta.usuario)
    }

    /** Simula la cuenta de Google del dispositivo: la crea la primera vez, sin cuestionario. */
    override suspend fun ingresarConGoogle(): ResultadoAuth {
        delay(RETRASO_SIMULADO_MS)
        val cuenta = cuentas.getOrPut(CORREO_GOOGLE) {
            Cuenta(Usuario(id = nuevoId(), nombre = "Checo", correo = CORREO_GOOGLE), contrasena = "")
        }
        return iniciarSesion(cuenta.usuario)
    }

    override suspend fun registrar(nombre: String, correo: String, contrasena: String): ResultadoAuth {
        delay(RETRASO_SIMULADO_MS)
        if (correo in cuentas) return ResultadoAuth.Fallo(ErrorCuenta.CORREO_YA_REGISTRADO)
        val usuario = Usuario(id = nuevoId(), nombre = nombre, correo = correo)
        cuentas[correo] = Cuenta(usuario, contrasena)
        return iniciarSesion(usuario)
    }

    override suspend fun restablecerContrasena(correo: String, nuevaContrasena: String): ResultadoAuth {
        delay(RETRASO_SIMULADO_MS)
        val cuenta = cuentas[correo] ?: return ResultadoAuth.Fallo(ErrorCuenta.CORREO_NO_REGISTRADO)
        cuentas[correo] = Cuenta(cuenta.usuario, nuevaContrasena)
        return iniciarSesion(cuenta.usuario)
    }

    override suspend fun guardarPerfilFisico(perfil: PerfilFisico) {
        val usuario = actual.value ?: return
        val actualizado = usuario.copy(perfil = perfil)
        cuentas[usuario.correo]?.let { cuentas[usuario.correo] = Cuenta(actualizado, it.contrasena) }
        actual.update { actualizado }
    }

    private fun iniciarSesion(usuario: Usuario): ResultadoAuth {
        actual.value = usuario
        return ResultadoAuth.Exito(usuario)
    }

    private fun nuevoId(): String = "usuario-${siguienteId++}"

    companion object {
        /** Cuenta de demostración con el cuestionario ya completo (entra directo a la app). */
        const val CORREO_DEMO = "checo@velok.app"
        const val CONTRASENA_DEMO = "velok2026"
        private const val CORREO_GOOGLE = "checo.google@gmail.com"
        private const val RETRASO_SIMULADO_MS = 600L
    }
}
