package com.velok.app.presentation.detalleejercicio

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo

data class DetalleEjercicioUiState(
    val cargando: Boolean = true,
    /** null cuando terminó de cargar y el ejercicio no existe. */
    val ejercicio: Ejercicio? = null,
    val metrica: MetricaProgreso = MetricaProgreso.PESO,
    val rango: RangoTiempo = RangoTiempo.MES,
    /** Un valor por sesión, en orden de fecha. */
    val progreso: List<Double> = emptyList(),
)
