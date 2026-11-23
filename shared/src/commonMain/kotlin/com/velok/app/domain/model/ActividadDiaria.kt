package com.velok.app.domain.model

import kotlinx.datetime.LocalDate

/**
 * Resumen de la actividad de un día.
 *
 * @property progreso avance hacia la meta diaria de pasos, de 0.0 a 1.0 (ver progresoDelDia).
 * @property pasos del día; null si no hay datos.
 * @property metaPasos meta del anillo (META_PASOS_DIARIOS).
 */
data class ActividadDiaria(
    val fecha: LocalDate,
    val progreso: Float,
    val pasos: Long? = null,
    val metaPasos: Int = META_PASOS_DIARIOS,
)
