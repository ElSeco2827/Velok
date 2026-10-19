package com.velok.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.categoria.CategoriaScreen
import com.velok.app.presentation.dashboard.DashboardScreen
import com.velok.app.presentation.detalleejercicio.DetalleEjercicioScreen
import com.velok.app.presentation.detalleplan.DetallePlanScreen
import com.velok.app.presentation.editarplan.EditarPlanScreen
import com.velok.app.presentation.ejercicio.EjercicioScreen
import com.velok.app.presentation.perfil.PerfilScreen
import com.velok.app.presentation.planes.PlanesScreen
import com.velok.app.presentation.progreso.ProgresoScreen
import com.velok.app.presentation.sesion.SesionScreen

@Composable
fun VelokNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val abrirEjercicio: (String) -> Unit = { navController.navigate(RutaDetalleEjercicio(it)) }
    val abrirPlan: (String) -> Unit = { navController.navigate(RutaDetallePlan(it)) }

    NavHost(
        navController = navController,
        startDestination = RutaInicio,
        modifier = modifier,
    ) {
        // Raíces de la barra inferior.
        composable<RutaInicio> {
            DashboardScreen(
                onVerTodosPlanes = { navController.navegarA(DestinoPrincipal.PLANES) },
                onAbrirPlan = abrirPlan,
            )
        }
        composable<RutaPlanes> {
            PlanesScreen(
                onAbrirPlan = abrirPlan,
                onEditarPlan = { navController.navigate(RutaEditarPlan(it)) },
            )
        }
        composable<RutaEjercicio> {
            EjercicioScreen(
                onAbrirEjercicio = abrirEjercicio,
                onAbrirCategoria = { navController.navigate(RutaCategoria(it.name)) },
            )
        }
        composable<RutaActividad> { ProgresoScreen() }
        composable<RutaPerfil> { PerfilScreen() }

        // Detalles.
        composable<RutaCategoria> { entrada ->
            CategoriaScreen(
                categoria = TipoCategoria.valueOf(entrada.toRoute<RutaCategoria>().categoria),
                onAbrirEjercicio = abrirEjercicio,
            )
        }
        composable<RutaDetallePlan> { entrada ->
            val planId = entrada.toRoute<RutaDetallePlan>().planId
            DetallePlanScreen(
                planId = planId,
                onAbrirEjercicio = abrirEjercicio,
                onEditar = { navController.navigate(RutaEditarPlan(planId)) },
                onIniciar = { navController.navigate(RutaSesion(planId)) },
            )
        }
        composable<RutaEditarPlan> { entrada ->
            EditarPlanScreen(
                planId = entrada.toRoute<RutaEditarPlan>().planId,
                onAbrirEjercicio = abrirEjercicio,
                // Al borrar el plan se vuelve a la lista (el detalle del plan ya no existe).
                onPlanBorrado = { navController.navegarA(DestinoPrincipal.PLANES, conservarEstado = false) },
            )
        }
        composable<RutaSesion> { entrada ->
            SesionScreen(
                planId = entrada.toRoute<RutaSesion>().planId,
                onAbrirEjercicio = abrirEjercicio,
                onSalir = { navController.popBackStack() },
            )
        }
        composable<RutaDetalleEjercicio> { entrada ->
            DetalleEjercicioScreen(ejercicioId = entrada.toRoute<RutaDetalleEjercicio>().ejercicioId)
        }
    }
}

/**
 * Navega a un destino principal. Con [conservarEstado] cada pestaña recuerda las pantallas que
 * tenía abiertas; sin él, la pestaña se abre desde su raíz.
 */
fun NavHostController.navegarA(destino: DestinoPrincipal, conservarEstado: Boolean = true) {
    navigate(destino.ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = conservarEstado }
        launchSingleTop = true
        restoreState = conservarEstado
    }
}

/** Destino principal si [this] es la raíz de una pestaña; null en pantallas de detalle. */
fun NavDestination?.destinoPrincipal(): DestinoPrincipal? =
    DestinoPrincipal.entries.firstOrNull { destino -> this?.hasRoute(destino.ruta::class) == true }
