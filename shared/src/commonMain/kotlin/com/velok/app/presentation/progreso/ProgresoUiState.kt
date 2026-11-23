package com.velok.app.presentation.progreso

import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.presentation.salud.EstadoTarjetaSalud

data class ProgresoUiState(
    /** Tarjeta de actividad (Health Connect / HealthKit), con su propio estado de carga y error. */
    val salud: EstadoTarjetaSalud = EstadoTarjetaSalud(),
    val metrica: MetricaProgreso = MetricaProgreso.VOLUMEN,
    val rango: RangoTiempo = RangoTiempo.MES,
    /** Un valor por sesión (todos los ejercicios), en orden de fecha (pesos ya en [unidad]). */
    val progreso: List<Double> = emptyList(),
    val unidad: UnidadPeso = UnidadPeso.KG,
)
