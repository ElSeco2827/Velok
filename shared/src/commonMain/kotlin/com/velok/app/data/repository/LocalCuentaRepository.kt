package com.velok.app.data.repository

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.UsuarioActualEntidad
import com.velok.app.data.local.aDominio
import com.velok.app.data.local.aEntidad
import com.velok.app.data.local.seguridad.HashContrasenas
import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.Usuario
import com.velok.app.domain.repository.AuthRepository
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Cuentas guardadas en la base de datos local. Implementa autenticación y usuario actual en la
 * misma clase porque comparten quién inició sesión.
 *
 * - Las contraseñas se guardan como hash PBKDF2 con sal ([HashContrasenas]), nunca en texto plano.
 * - Quién inició sesión se guarda en la tabla usuario_actual: al volver a abrir la app la sesión
 *   sigue iniciada hasta que el usuario la cierra.
 */
class LocalCuentaRepository(
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
    private val hash: HashContrasenas,
) : AuthRepository, UsuarioRepository {

    private val dao = baseDatos.cuentaDao()

    override fun observarUsuarioActual(): Flow<Usuario?> = flow {
        sembrador.asegurarDatos()
        emitAll(dao.observarActual().map { it?.aDominio() })
    }

    override suspend fun ingresar(correo: String, contrasena: String): ResultadoAuth {
        sembrador.asegurarDatos()
        val cuenta = dao.porCorreo(correo)
        // Si la cuenta no existe se calcula un hash de todos modos: así el tiempo de respuesta no
        // revela qué correos están registrados. Mismo error en ambos casos por la misma razón.
        val valida = calcular { hash.verificar(contrasena, cuenta?.hashContrasena ?: HASH_SENUELO) }
        if (cuenta == null || !valida) return ResultadoAuth.Fallo(ErrorCuenta.CREDENCIALES_INCORRECTAS)
        return iniciarSesion(cuenta.id)
    }

    /** Simula la cuenta de Google del dispositivo: la crea la primera vez (sin contraseña propia). */
    override suspend fun ingresarConGoogle(): ResultadoAuth {
        sembrador.asegurarDatos()
        val cuenta = dao.porCorreo(DatosMock.CORREO_GOOGLE)
            ?: Usuario(id = nuevoId(), nombre = "Checo", correo = DatosMock.CORREO_GOOGLE)
                .aEntidad(hashContrasena = "")
                .also { dao.insertar(it) }
        return iniciarSesion(cuenta.id)
    }

    override suspend fun registrar(nombre: String, correo: String, contrasena: String): ResultadoAuth {
        sembrador.asegurarDatos()
        if (dao.porCorreo(correo) != null) return ResultadoAuth.Fallo(ErrorCuenta.CORREO_YA_REGISTRADO)
        val usuario = Usuario(id = nuevoId(), nombre = nombre, correo = correo)
        dao.insertar(usuario.aEntidad(hashContrasena = calcular { hash.generar(contrasena) }))
        return iniciarSesion(usuario.id)
    }

    override suspend fun restablecerContrasena(correo: String, nuevaContrasena: String): ResultadoAuth {
        sembrador.asegurarDatos()
        val cuenta = dao.porCorreo(correo) ?: return ResultadoAuth.Fallo(ErrorCuenta.CORREO_NO_REGISTRADO)
        dao.actualizar(cuenta.copy(hashContrasena = calcular { hash.generar(nuevaContrasena) }))
        return iniciarSesion(cuenta.id)
    }

    override suspend fun guardarPerfilFisico(perfil: PerfilFisico) {
        sembrador.asegurarDatos()
        val actual = dao.observarActual().first() ?: return
        dao.actualizar(actual.aDominio().copy(perfil = perfil).aEntidad(actual.hashContrasena))
    }

    override suspend fun cerrarSesion() {
        dao.limpiarActual()
    }

    private suspend fun iniciarSesion(id: String): ResultadoAuth {
        dao.fijarActual(UsuarioActualEntidad(usuarioId = id))
        val usuario = requireNotNull(dao.porId(id)).aDominio()
        return ResultadoAuth.Exito(usuario)
    }

    /** El hash tarda a propósito: se calcula fuera del hilo principal para no congelar la pantalla. */
    private suspend fun <T> calcular(bloque: () -> T): T = withContext(Dispatchers.Default) { bloque() }

    @OptIn(ExperimentalUuidApi::class)
    private fun nuevoId(): String = "usuario-${Uuid.random()}"

    private companion object {
        /** Hash con formato válido de una contraseña que nadie conoce; solo sirve para igualar tiempos. */
        val HASH_SENUELO = listOf(
            HashContrasenas.ALGORITMO,
            HashContrasenas.ITERACIONES.toString(),
            "AAAAAAAAAAAAAAAAAAAAAA==",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
        ).joinToString("$")
    }
}
