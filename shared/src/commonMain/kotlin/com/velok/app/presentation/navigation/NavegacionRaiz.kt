package com.velok.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.presentation.bienvenida.BienvenidaScreen
import com.velok.app.presentation.carga.CargaScreen
import com.velok.app.presentation.formulario.FormularioScreen
import com.velok.app.presentation.ingreso.IngresoScreen
import com.velok.app.presentation.legal.DocumentoLegal
import com.velok.app.presentation.legal.DocumentoLegalScreen
import com.velok.app.presentation.recuperacion.RecuperacionScreen
import com.velok.app.presentation.registro.RegistroScreen
import kotlinx.serialization.Serializable

// Rutas de la navegación raíz (antes de entrar a las pantallas principales).
@Serializable data object RutaCarga
@Serializable data object RutaBienvenida
@Serializable data object RutaIngreso
@Serializable data object RutaRegistro
@Serializable data object RutaRecuperacion
@Serializable data object RutaFormulario
@Serializable data object RutaPrincipal

/** [documento] es el nombre de un [DocumentoLegal]. */
@Serializable data class RutaDocumentoLegal(val documento: String)

/**
 * Navegación de toda la app: carga → cuenta (bienvenida, ingreso, registro, recuperación,
 * textos legales) → cuestionario inicial → pantallas principales.
 */
@Composable
fun NavegacionRaiz(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val irA: (DestinoInicial) -> Unit = { navController.irA(it) }
    val abrirDocumento: (DocumentoLegal) -> Unit = { navController.navigate(RutaDocumentoLegal(it.name)) }

    NavHost(navController = navController, startDestination = RutaCarga, modifier = modifier) {
        composable<RutaCarga> { CargaScreen(onDestino = irA) }
        composable<RutaBienvenida> {
            BienvenidaScreen(
                onRegistroCorreo = { navController.navigate(RutaRegistro) },
                onIniciarSesion = { navController.navigate(RutaIngreso) },
                onListo = irA,
            )
        }
        composable<RutaIngreso> {
            IngresoScreen(onRecuperar = { navController.navigate(RutaRecuperacion) }, onListo = irA)
        }
        composable<RutaRegistro> {
            RegistroScreen(
                onTerminos = { abrirDocumento(DocumentoLegal.TERMINOS) },
                onPrivacidad = { abrirDocumento(DocumentoLegal.PRIVACIDAD) },
                onListo = irA,
            )
        }
        composable<RutaRecuperacion> { RecuperacionScreen(onListo = irA) }
        composable<RutaDocumentoLegal> { entrada ->
            DocumentoLegalScreen(
                documento = DocumentoLegal.valueOf(entrada.toRoute<RutaDocumentoLegal>().documento),
                onContinuar = { navController.popBackStack() },
            )
        }
        composable<RutaFormulario> { FormularioScreen(onTerminado = { irA(DestinoInicial.PRINCIPAL) }) }
        // Al cerrar sesión se vuelve a la selección de inicio y se borra la pila (irA), así
        // "atrás" no regresa a las pantallas de la cuenta anterior.
        composable<RutaPrincipal> { PantallaPrincipal(onSesionCerrada = { irA(DestinoInicial.BIENVENIDA) }) }
    }
}

/**
 * Va al destino y borra todo lo anterior: después de iniciar sesión, "atrás" no debe regresar al
 * formulario de cuenta ni a la pantalla de carga.
 */
private fun NavHostController.irA(destino: DestinoInicial) {
    val ruta: Any = when (destino) {
        DestinoInicial.BIENVENIDA -> RutaBienvenida
        DestinoInicial.FORMULARIO -> RutaFormulario
        DestinoInicial.PRINCIPAL -> RutaPrincipal
    }
    navigate(ruta) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
