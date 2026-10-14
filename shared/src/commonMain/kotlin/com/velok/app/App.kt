package com.velok.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.velok.app.presentation.components.VelokBottomBar
import com.velok.app.presentation.components.VelokHeader
import com.velok.app.presentation.navigation.DestinoPrincipal
import com.velok.app.presentation.navigation.VelokNavHost
import com.velok.app.presentation.navigation.destinoPrincipal
import com.velok.app.presentation.navigation.navegarA
import com.velok.app.presentation.theme.VelokTheme

@Composable
fun App() {
    VelokTheme {
        val navController = rememberNavController()
        val entradaActual by navController.currentBackStackEntryAsState()
        val destinoActual = entradaActual?.destination.destinoPrincipal()

        // Ejercicio usa fondo blanco; el resto, crema (como en el mockup).
        val colores = VelokTheme.colores
        val fondo by animateColorAsState(
            if (destinoActual == DestinoPrincipal.EJERCICIO) colores.superficie else colores.fondo,
        )

        Scaffold(
            containerColor = fondo,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                VelokHeader(tieneNotificaciones = true, onNotificacionesClick = {})
            },
            bottomBar = {
                VelokBottomBar(
                    destinos = DestinoPrincipal.entries,
                    seleccionado = destinoActual,
                    onSeleccionar = navController::navegarA,
                )
            },
        ) { padding ->
            VelokNavHost(navController = navController, modifier = Modifier.padding(padding))
        }
    }
}
