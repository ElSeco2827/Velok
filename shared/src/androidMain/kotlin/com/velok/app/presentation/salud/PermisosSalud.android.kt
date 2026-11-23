package com.velok.app.presentation.salud

import android.content.ActivityNotFoundException
import android.content.Intent
import android.health.connect.HealthConnectManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.PermissionController
import com.velok.app.data.salud.PERMISOS_HEALTH_CONNECT

// Implementación Android de expect/actual: permisos de Health Connect.

@Composable
actual fun rememberSolicitarPermisosSalud(alTerminar: () -> Unit): () -> Unit {
    val terminar by rememberUpdatedState(alTerminar)
    // El resultado (permisos concedidos) no se usa aquí: el repositorio los vuelve a revisar.
    val lanzador = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract(),
    ) { terminar() }
    return remember(lanzador) { { lanzador.launch(PERMISOS_HEALTH_CONNECT) } }
}

@Composable
actual fun rememberAbrirAjustesSalud(): (() -> Unit)? {
    val contexto = LocalContext.current
    return remember(contexto) {
        {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14+: los permisos de Health Connect de esta app, dentro del sistema.
                Intent(HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS)
                    .putExtra(Intent.EXTRA_PACKAGE_NAME, contexto.packageName)
            } else {
                // Android 9-13: ajustes de la app Health Connect (acción que usa su librería).
                Intent(ACCION_AJUSTES_HEALTH_CONNECT)
            }
            try {
                contexto.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: ActivityNotFoundException) {
                // Sin Health Connect no hay ajustes que abrir; la tarjeta ya ofrece instalarlo.
            }
        }
    }
}

/** Ajustes de la app Health Connect en Android 9 a 13. */
private const val ACCION_AJUSTES_HEALTH_CONNECT = "androidx.health.ACTION_HEALTH_CONNECT_SETTINGS"
