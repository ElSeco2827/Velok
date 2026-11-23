package com.velok.app.presentation.dashboard

import com.velok.app.data.repository.MockActividadRepository
import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.ResumenSalud
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.ActividadRepository
import com.velok.app.domain.repository.PlanRepository
import com.velok.app.domain.usecase.ObtenerActividadSaludUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** La tarjeta de actividad de Inicio con un repositorio falso en cada estado posible. */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @BeforeTest
    fun antes() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun despues() = Dispatchers.resetMain()

    /** Planes falsos sin imágenes (las de DatosMock son recursos que necesitan Android). */
    private val planes = object : PlanRepository {
        private val lista = listOf(Plan("p1", "Set del Lunes", TipoCategoria.PECHO, "", LocalDate(2026, 11, 16), emptyList()))
        override fun observarPlanes(): Flow<List<Plan>> = flowOf(lista)
        override fun observarPlan(id: String): Flow<Plan?> = flowOf(lista.firstOrNull { it.id == id })
        override suspend fun guardarPlan(plan: Plan) = Unit
        override suspend fun eliminarPlan(id: String) = Unit
    }

    private fun viewModel(repositorio: ActividadRepository) = DashboardViewModel(
        obtenerActividadSalud = ObtenerActividadSaludUseCase(repositorio),
        obtenerPlanes = ObtenerPlanesUseCase(planes),
    )

    @Test
    fun conPermisoMuestraLosDatos() = runTest {
        val vm = viewModel(MockActividadRepository(AccesoSalud.DISPONIBLE, retrasoMs = 0))
        val salud = vm.estado.value.salud
        assertFalse(salud.cargando)
        assertIs<ActividadSalud.Datos>(salud.actividad)
        assertTrue(vm.estado.value.planes.isNotEmpty())
    }

    @Test
    fun sinPermisoOfreceConectarSinNumeros() = runTest {
        val vm = viewModel(MockActividadRepository(AccesoSalud.SIN_PERMISO, retrasoMs = 0))
        val salud = vm.estado.value.salud
        assertEquals(ActividadSalud.SinPermiso, salud.actividad)
        assertFalse(salud.permisoRechazado)
    }

    @Test
    fun siRechazaElPermisoSeMuestraElMensajeYLuegoPuedeConectar() = runTest {
        val repositorio = MockActividadRepository(AccesoSalud.SIN_PERMISO, retrasoMs = 0)
        val vm = viewModel(repositorio)

        // Se cerró el diálogo de permisos sin aceptar.
        vm.cargarSalud(trasSolicitarPermiso = true)
        assertTrue(vm.estado.value.salud.permisoRechazado)
        // Al volver a la app (onResume) se mantiene el mensaje.
        vm.cargarSalud()
        assertTrue(vm.estado.value.salud.permisoRechazado)

        // Lo da desde los ajustes y vuelve a la app: aparecen los datos.
        repositorio.acceso = AccesoSalud.DISPONIBLE
        vm.cargarSalud()
        assertIs<ActividadSalud.Datos>(vm.estado.value.salud.actividad)
        assertFalse(vm.estado.value.salud.permisoRechazado)
    }

    @Test
    fun noDisponible() = runTest {
        val vm = viewModel(MockActividadRepository(AccesoSalud.NO_DISPONIBLE, retrasoMs = 0))
        assertEquals(ActividadSalud.NoDisponible, vm.estado.value.salud.actividad)
    }

    @Test
    fun requiereInstalarHealthConnect() = runTest {
        val vm = viewModel(MockActividadRepository(AccesoSalud.REQUIERE_INSTALAR, retrasoMs = 0))
        assertEquals(ActividadSalud.RequiereInstalar, vm.estado.value.salud.actividad)
    }

    @Test
    fun unErrorAlLeerNoCierraLaAppYLosPlanesSeSiguenViendo() = runTest {
        val falla = object : ActividadRepository {
            override suspend fun obtenerAcceso() = AccesoSalud.DISPONIBLE
            override suspend fun obtenerResumen(fecha: LocalDate): ResumenSalud = throw SecurityException("Permiso quitado")
        }
        val vm = viewModel(falla)
        assertTrue(vm.estado.value.salud.error)
        assertFalse(vm.estado.value.salud.cargando)
        assertTrue(vm.estado.value.planes.isNotEmpty())
    }
}
