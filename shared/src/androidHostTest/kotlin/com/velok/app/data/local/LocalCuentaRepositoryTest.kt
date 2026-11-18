package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.UnidadPeso
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalCuentaRepositoryTest {

    private val repos = Repositorios(baseDatosEnMemoria())
    private val cuentas = repos.cuentas

    @After
    fun cerrar() = repos.baseDatos.close()

    @Test
    fun laCuentaDemoEntraConSuContrasena() = runTest {
        val resultado = cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO)
        assertEquals(DatosMock.usuarioDemo, assertIs<ResultadoAuth.Exito>(resultado).usuario)
        assertEquals(DatosMock.usuarioDemo, cuentas.observarUsuarioActual().first())
    }

    @Test
    fun laContrasenaSeGuardaComoHash() = runTest {
        cuentas.registrar("Ana", "ana@velok.app", "secreta123")
        val demo = requireNotNull(repos.baseDatos.cuentaDao().porCorreo(DatosMock.CORREO_DEMO))
        val ana = requireNotNull(repos.baseDatos.cuentaDao().porCorreo("ana@velok.app"))

        for ((cuenta, contrasena) in listOf(demo to DatosMock.CONTRASENA_DEMO, ana to "secreta123")) {
            assertTrue(cuenta.hashContrasena.startsWith("pbkdf2-sha256$"))
            assertFalse(contrasena in cuenta.hashContrasena)
        }
    }

    @Test
    fun rechazaContrasenaIncorrectaYCorreoInexistenteConElMismoError() = runTest {
        val incorrecta = cuentas.ingresar(DatosMock.CORREO_DEMO, "otra")
        val inexistente = cuentas.ingresar("nadie@velok.app", DatosMock.CONTRASENA_DEMO)
        assertEquals(ResultadoAuth.Fallo(ErrorCuenta.CREDENCIALES_INCORRECTAS), incorrecta)
        assertEquals(ResultadoAuth.Fallo(ErrorCuenta.CREDENCIALES_INCORRECTAS), inexistente)
        assertNull(cuentas.observarUsuarioActual().first())
    }

    @Test
    fun registraUnaCuentaYNoPermiteRepetirElCorreo() = runTest {
        val registro = assertIs<ResultadoAuth.Exito>(cuentas.registrar("Ana", "ana@velok.app", "secreta123"))
        assertNull(registro.usuario.perfil)
        assertEquals(registro.usuario, cuentas.observarUsuarioActual().first())

        assertEquals(
            ResultadoAuth.Fallo(ErrorCuenta.CORREO_YA_REGISTRADO),
            cuentas.registrar("Otra Ana", "ana@velok.app", "otra12345"),
        )
        cuentas.cerrarSesion()
        assertIs<ResultadoAuth.Exito>(cuentas.ingresar("ana@velok.app", "secreta123"))
    }

    @Test
    fun guardaElPerfilFisicoDelUsuarioActual() = runTest {
        cuentas.registrar("Ana", "ana@velok.app", "secreta123")
        val perfil = PerfilFisico(LocalDate(2000, 1, 15), UnidadPeso.LB, 160, 60.5, MetaEntrenamiento.entries.first())
        cuentas.guardarPerfilFisico(perfil)

        assertEquals(perfil, cuentas.observarUsuarioActual().first()?.perfil)
        // La contraseña sigue funcionando después de actualizar la cuenta.
        cuentas.cerrarSesion()
        assertIs<ResultadoAuth.Exito>(cuentas.ingresar("ana@velok.app", "secreta123"))
    }

    @Test
    fun restablecerCambiaLaContrasena() = runTest {
        assertIs<ResultadoAuth.Exito>(cuentas.restablecerContrasena(DatosMock.CORREO_DEMO, "nueva2026"))
        cuentas.cerrarSesion()

        assertIs<ResultadoAuth.Fallo>(cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO))
        assertIs<ResultadoAuth.Exito>(cuentas.ingresar(DatosMock.CORREO_DEMO, "nueva2026"))
        assertEquals(
            ResultadoAuth.Fallo(ErrorCuenta.CORREO_NO_REGISTRADO),
            cuentas.restablecerContrasena("nadie@velok.app", "nueva2026"),
        )
    }

    @Test
    fun googleCreaLaCuentaUnaSolaVez() = runTest {
        val primera = assertIs<ResultadoAuth.Exito>(cuentas.ingresarConGoogle()).usuario
        cuentas.cerrarSesion()
        val segunda = assertIs<ResultadoAuth.Exito>(cuentas.ingresarConGoogle()).usuario
        assertEquals(primera, segunda)
        assertNotEquals(DatosMock.usuarioDemo.id, primera.id)
    }

    @Test
    fun cerrarSesionEmiteNull() = runTest {
        val emisiones = Emisiones(backgroundScope, cuentas.observarUsuarioActual())
        emisiones.esperar { it == null }
        cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO)
        emisiones.esperar { it == DatosMock.usuarioDemo }
        cuentas.cerrarSesion()
        emisiones.esperar { it == null }
    }
}
