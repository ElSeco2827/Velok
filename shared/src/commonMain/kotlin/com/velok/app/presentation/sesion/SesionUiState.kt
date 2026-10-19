package com.velok.app.presentation.sesion

import com.velok.app.domain.model.SesionEntrenamiento
import kotlin.time.Instant

/** Descanso en curso tras completar una serie del ejercicio [indiceEjercicio]. */
data class DescansoActivo(val indiceEjercicio: Int, val hasta: Instant)

data class SesionUiState(
    val cargando: Boolean = true,
    /** null cuando terminó de cargar y el plan no existe. */
    val sesion: SesionEntrenamiento? = null,
    /** Hora actual; se actualiza cada segundo para el cronómetro y el descanso. */
    val ahora: Instant,
    val descanso: DescansoActivo? = null,
    /** Se pidió terminar sin series completadas: hay que confirmar que se sale sin guardar. */
    val confirmarSalida: Boolean = false,
)
