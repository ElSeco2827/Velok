package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/** Simula cerrar y volver a abrir la app: una base de datos en archivo que se cierra y se reabre. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersistenciaTest {

    @Test
    fun losDatosYLaSesionSobrevivenAlReabrir() = runTest {
        val nuevo = DatosMock.planes.first().copy(id = "plan-nuevo", nombre = "Plan que persiste")

        val primeraVez = Repositorios(baseDatosEnArchivo("persistencia.db"))
        primeraVez.cuentas.ingresar(DatosMock.CORREO_DEMO, DatosMock.CONTRASENA_DEMO)
        primeraVez.planes.guardarPlan(nuevo)
        primeraVez.baseDatos.close()

        val segundaVez = Repositorios(baseDatosEnArchivo("persistencia.db"))
        assertEquals(DatosMock.usuarioDemo, segundaVez.cuentas.observarUsuarioActual().first())
        assertEquals(nuevo, segundaVez.planes.observarPlan("plan-nuevo").first())
        segundaVez.baseDatos.close()
    }

    @Test
    fun noSiembraDosVecesNiDevuelveLosPlanesBorrados() = runTest {
        val primeraVez = Repositorios(baseDatosEnArchivo("siembra.db"))
        DatosMock.planes.forEach { primeraVez.planes.eliminarPlan(it.id) }
        primeraVez.baseDatos.close()

        val segundaVez = Repositorios(baseDatosEnArchivo("siembra.db"))
        assertEquals(emptyList(), segundaVez.planes.observarPlanes().first())
        assertEquals(DatosMock.ejercicios, segundaVez.ejercicios.obtenerEjercicios())
        assertEquals(DatosMock.historial(relojFijo).size, segundaVez.sesiones.observarHistorial().first().size)
        segundaVez.baseDatos.close()
    }
}
