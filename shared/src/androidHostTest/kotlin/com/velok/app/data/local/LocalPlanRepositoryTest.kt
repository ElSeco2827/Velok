package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoSerie
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalPlanRepositoryTest {

    private val repos = Repositorios(baseDatosEnMemoria())
    private val planes = repos.planes

    @After
    fun cerrar() = repos.baseDatos.close()

    @Test
    fun laPrimeraVezTieneLosPlanesDeEjemplo() = runTest {
        assertEquals(DatosMock.planes, planes.observarPlanes().first())
    }

    @Test
    fun guardaUnPlanNuevoAlFinal() = runTest {
        val nuevo = DatosMock.planes.first().copy(id = "plan-nuevo", nombre = "Pierna pesada")
        planes.guardarPlan(nuevo)

        val lista = planes.observarPlanes().first()
        assertEquals(DatosMock.planes.size + 1, lista.size)
        assertEquals(nuevo, lista.last())
        assertEquals(nuevo, planes.observarPlan("plan-nuevo").first())
    }

    @Test
    fun actualizaUnPlanSinCambiarSuLugar() = runTest {
        val original = DatosMock.planes[1]
        val primerEjercicio = original.ejercicios.first()
        val editado = original.copy(
            nombre = "Martes editado",
            // Quita el último ejercicio y cambia las series del primero.
            ejercicios = listOf(
                primerEjercicio.copy(series = listOf(Serie(1, TipoSerie.CALENTAMIENTO, 20.0, 15), Serie(2, TipoSerie.NORMAL, 40.0, 8))),
            ) + original.ejercicios.drop(1).dropLast(1),
        )
        planes.guardarPlan(editado)

        val lista = planes.observarPlanes().first()
        assertEquals(DatosMock.planes.size, lista.size)
        assertEquals(editado, lista[1])
    }

    @Test
    fun eliminaElPlanPeroConservaElHistorial() = runTest {
        val historialAntes = repos.sesiones.observarHistorial().first()
        planes.eliminarPlan("plan-lunes")

        assertTrue(planes.observarPlanes().first().none { it.id == "plan-lunes" })
        assertNull(planes.observarPlan("plan-lunes").first())
        assertEquals(historialAntes, repos.sesiones.observarHistorial().first())
    }

    @Test
    fun elFlowEmiteLosCambios() = runTest {
        val emisiones = Emisiones(backgroundScope, planes.observarPlanes())
        emisiones.esperar { it.size == DatosMock.planes.size }

        planes.guardarPlan(DatosMock.planes.first().copy(id = "plan-nuevo"))
        emisiones.esperar { lista -> lista.any { it.id == "plan-nuevo" } }

        planes.eliminarPlan("plan-nuevo")
        val despues = emisiones.esperar { lista -> lista.none { it.id == "plan-nuevo" } }
        assertEquals(DatosMock.planes, despues)
    }
}
