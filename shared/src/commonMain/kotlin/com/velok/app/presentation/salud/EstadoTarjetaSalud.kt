package com.velok.app.presentation.salud

import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.usecase.ObtenerActividadSaludUseCase
import kotlin.coroutines.cancellation.CancellationException

/**
 * Estado de la tarjeta "Actividad diaria" (Inicio y Progreso). La tarjeta tiene su propio estado
 * de carga y error: si los datos de salud fallan, el resto de la pantalla sigue funcionando.
 *
 * @property permisoRechazado se pidió el permiso y el usuario no lo dio: se muestra un mensaje en
 *   lugar de volver a ofrecer "Conectar" como si nada.
 */
data class EstadoTarjetaSalud(
    val cargando: Boolean = true,
    val error: Boolean = false,
    val actividad: ActividadSalud? = null,
    val permisoRechazado: Boolean = false,
)

/**
 * Vuelve a leer los datos de salud. [trasSolicitarPermiso] indica que se acaba de cerrar el
 * diálogo de permisos: si sigue sin permiso, el usuario lo rechazó.
 */
suspend fun EstadoTarjetaSalud.recargar(
    obtener: ObtenerActividadSaludUseCase,
    trasSolicitarPermiso: Boolean = false,
): EstadoTarjetaSalud = try {
    val actividad = obtener()
    val sinPermiso = actividad is ActividadSalud.SinPermiso
    copy(
        cargando = false,
        error = false,
        actividad = actividad,
        permisoRechazado = sinPermiso && (trasSolicitarPermiso || permisoRechazado),
    )
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    // Por ejemplo, el permiso se quitó a mitad de la lectura: la tarjeta lo dice y nada se cierra.
    copy(cargando = false, error = true)
}

/** Lo que el usuario puede hacer desde la tarjeta según el estado. */
class AccionesSalud(
    val onConectar: () -> Unit = {},
    val onAbrirTienda: () -> Unit = {},
    /** null si la plataforma no permite abrir sus ajustes de salud (iOS). */
    val onAbrirAjustes: (() -> Unit)? = null,
    val onReintentar: () -> Unit = {},
)
