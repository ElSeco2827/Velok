package com.velok.app.presentation.salud

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Código específico de plataforma (expect/actual): pedir permiso para LEER los datos de salud.
 * - Android: abre el diálogo de Health Connect con un ActivityResultLauncher.
 * - iOS: llama a HKHealthStore.requestAuthorization (solo tipos de lectura).
 * Devuelve la función que inicia la solicitud; [alTerminar] se llama al cerrar el diálogo.
 */
@Composable
expect fun rememberSolicitarPermisosSalud(alTerminar: () -> Unit): () -> Unit

/**
 * Código específico de plataforma (expect/actual): abrir los ajustes donde el usuario da o quita
 * el permiso. Android: ajustes de Health Connect. iOS: null, porque Apple no permite abrir
 * directamente los permisos de Salud de una app (se cambian en la app Salud).
 */
@Composable
expect fun rememberAbrirAjustesSalud(): (() -> Unit)?

/** Health Connect en Google Play (Android 9 a 13 lo necesitan como app aparte). */
private const val URL_HEALTH_CONNECT = "https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"

/**
 * Arma las acciones de la tarjeta para una pantalla. [recargar] recibe true cuando se acaba de
 * cerrar el diálogo de permisos. Al volver a la app (por ejemplo desde los ajustes o la tienda)
 * se recarga sola, por si el usuario dio el permiso o instaló Health Connect.
 */
@Composable
fun rememberAccionesSalud(recargar: (trasSolicitarPermiso: Boolean) -> Unit): AccionesSalud {
    val solicitar = rememberSolicitarPermisosSalud(alTerminar = { recargar(true) })
    val abrirAjustes = rememberAbrirAjustesSalud()
    val uri = LocalUriHandler.current
    LifecycleResumeEffect(Unit) {
        recargar(false)
        onPauseOrDispose { }
    }
    return AccionesSalud(
        onConectar = solicitar,
        onAbrirTienda = { runCatching { uri.openUri(URL_HEALTH_CONNECT) } },
        onAbrirAjustes = abrirAjustes,
        onReintentar = { recargar(false) },
    )
}
