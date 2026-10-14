package com.velok.app.domain.model

import kotlinx.datetime.LocalDate

/**
 * Resumen de la actividad de un día.
 *
 * @property progreso avance hacia la meta diaria, de 0.0 a 1.0.
 */
data class ActividadDiaria(
    val fecha: LocalDate,
    val progreso: Float,
)
