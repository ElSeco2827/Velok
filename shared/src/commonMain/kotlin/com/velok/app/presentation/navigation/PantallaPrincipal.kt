package com.velok.app.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.velok.app.presentation.components.VelokBottomBar
import com.velok.app.presentation.components.VelokHeader
import com.velok.app.presentation.theme.VelokTheme

/**
 * Pantallas principales (después de iniciar sesión): encabezado, barra inferior y su propia
 * navegación entre pestañas. [onSesionCerrada] lo maneja la navegación raíz (vuelve al inicio).
 */
@Composable
fun PantallaPrincipal(onSesionCerrada: () -> Unit) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val raizActual = entradaActual?.destination.destinoPrincipal()

    // En pantallas de detalle la barra sigue marcando la pestaña desde la que se abrieron.
    var ultimaPestana by rememberSaveable { mutableStateOf(DestinoPrincipal.INICIO) }
    SideEffect { if (raizActual != null) ultimaPestana = raizActual }
    val pestanaSeleccionada = raizActual ?: ultimaPestana

    Scaffold(
        containerColor = VelokTheme.colores.fondo,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            VelokHeader(
                tieneNotificaciones = true,
                onNotificacionesClick = {},
                onAtras = if (raizActual == null && entradaActual != null) {
                    { navController.popBackStack() }
                } else {
                    null
                },
            )
        },
        bottomBar = {
            VelokBottomBar(
                destinos = DestinoPrincipal.entries,
                seleccionado = pestanaSeleccionada,
                onSeleccionar = { destino ->
                    // Se marca aquí porque al volver a una pestaña se puede restaurar una pantalla
                    // de detalle (no la raíz), y entonces raizActual sería null.
                    ultimaPestana = destino
                    navController.navegarA(destino)
                },
            )
        },
    ) { padding ->
        VelokNavHost(
            navController = navController,
            onSesionCerrada = onSesionCerrada,
            modifier = Modifier.padding(padding),
        )
    }
}
