package com.velok.app.presentation.salud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.velok.app.data.salud.HealthKitVelok
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Implementación iOS de expect/actual: permisos de HealthKit.

@Composable
actual fun rememberSolicitarPermisosSalud(alTerminar: () -> Unit): () -> Unit {
    val terminar by rememberUpdatedState(alTerminar)
    val alcance = rememberCoroutineScope()
    return remember(alcance) {
        {
            alcance.launch {
                // typesToShare = null: la app no pide permiso de escritura, solo de lectura.
                suspendCancellableCoroutine { continuacion ->
                    HealthKitVelok.almacen.requestAuthorizationToShareTypes(null, HealthKitVelok.tiposLectura) { _, _ ->
                        continuacion.resume(Unit)
                    }
                }
                launch(Dispatchers.Main) { terminar() }
            }
        }
    }
}

/** Apple no permite abrir directamente los permisos de Salud de una app: se cambian en la app Salud. */
@Composable
actual fun rememberAbrirAjustesSalud(): (() -> Unit)? = null
