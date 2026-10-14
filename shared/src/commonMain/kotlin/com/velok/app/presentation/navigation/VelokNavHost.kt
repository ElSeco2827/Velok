package com.velok.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.velok.app.getPlatform
import com.velok.app.presentation.components.PlaceholderScreen
import com.velok.app.presentation.dashboard.DashboardScreen
import com.velok.app.presentation.ejercicio.EjercicioScreen
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.nav_actividad
import velok.shared.generated.resources.nav_calendario
import velok.shared.generated.resources.nav_perfil
import velok.shared.generated.resources.plataforma_actual

@Composable
fun VelokNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RutaInicio,
        modifier = modifier,
    ) {
        composable<RutaInicio> {
            DashboardScreen(onVerTodosPlanes = { navController.navegarA(DestinoPrincipal.EJERCICIO) })
        }
        composable<RutaCalendario> {
            PlaceholderScreen(titulo = stringResource(Res.string.nav_calendario))
        }
        composable<RutaEjercicio> {
            EjercicioScreen()
        }
        composable<RutaActividad> {
            PlaceholderScreen(titulo = stringResource(Res.string.nav_actividad))
        }
        composable<RutaPerfil> {
            // Ejemplo visible de expect/actual: el nombre viene de androidMain o iosMain.
            val plataforma = remember { getPlatform().name }
            PlaceholderScreen(
                titulo = stringResource(Res.string.nav_perfil),
                detalle = stringResource(Res.string.plataforma_actual, plataforma),
            )
        }
    }
}

/** Navega a un destino principal conservando el estado de cada pestaña. */
fun NavHostController.navegarA(destino: DestinoPrincipal) {
    navigate(destino.ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavDestination?.destinoPrincipal(): DestinoPrincipal? =
    DestinoPrincipal.entries.firstOrNull { destino ->
        this?.hierarchy?.any { it.hasRoute(destino.ruta::class) } == true
    }
