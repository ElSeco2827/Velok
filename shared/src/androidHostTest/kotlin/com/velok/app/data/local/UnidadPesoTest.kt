package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.usecase.CambiarUnidadPesoUseCase
import com.velok.app.domain.usecase.ObtenerUnidadPesoUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/** Cambiar la unidad de peso desde Perfil: se guarda en la base de datos y se emite al instante. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UnidadPesoTest {

    @Test
    fun seGuardaYSeEmiteAlInstante() = runTest {
        val repos = Repositorios(baseDatosEnMemoria())
        try {
            val obtener = ObtenerUnidadPesoUseCase(repos.cuentas)
            val cambiar = CambiarUnidadPesoUseCase(repos.cuentas)
            repos.cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO)

            val emisiones = Emisiones(backgroundScope, obtener())
            emisiones.esperar { it == UnidadPeso.KG }
            cambiar(UnidadPeso.LB)
            emisiones.esperar { it == UnidadPeso.LB }

            // Solo cambia la unidad: el peso corporal sigue guardado en kg.
            val perfil = repos.cuentas.observarUsuarioActual().first()?.perfil
            assertEquals(UnidadPeso.LB, perfil?.unidadPeso)
            assertEquals(DatosMock.usuarioDemo.perfil?.pesoKg, perfil?.pesoKg)
            assertEquals(DatosMock.usuarioDemo.perfil?.copy(unidadPeso = UnidadPeso.LB), perfil)
        } finally {
            repos.cerrar()
        }
    }

    @Test
    fun sobreviveAlCerrarLaApp() = runTest {
        val primeraVez = Repositorios(baseDatosEnArchivo("unidad.db"))
        primeraVez.cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO)
        CambiarUnidadPesoUseCase(primeraVez.cuentas)(UnidadPeso.LB)
        primeraVez.cerrar()

        val segundaVez = Repositorios(baseDatosEnArchivo("unidad.db"))
        try {
            assertEquals(UnidadPeso.LB, ObtenerUnidadPesoUseCase(segundaVez.cuentas)().first())
        } finally {
            segundaVez.cerrar()
        }
    }

    @Test
    fun sinPerfilSeUsaKgYNoSeGuardaNada() = runTest {
        val repos = Repositorios(baseDatosEnMemoria())
        try {
            repos.cuentas.registrar("Ana", "ana@velok.app", "secreta123")
            CambiarUnidadPesoUseCase(repos.cuentas)(UnidadPeso.LB)
            assertEquals(UnidadPeso.KG, ObtenerUnidadPesoUseCase(repos.cuentas)().first())
            assertEquals(null, repos.cuentas.observarUsuarioActual().first()?.perfil)
        } finally {
            repos.cerrar()
        }
    }
}
