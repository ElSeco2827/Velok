package com.velok.app.presentation.progreso

import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.UnidadPeso

data class ProgresoUiState(
    val cargando: Boolean = true,
    val error: Boolean = false,
    val actividad: ActividadDiaria? = null,
    val metricas: List<Metrica> = emptyList(),
    val metrica: MetricaProgreso = MetricaProgreso.VOLUMEN,
    val rango: RangoTiempo = RangoTiempo.MES,
    /** Un valor por sesión (todos los ejercicios), en orden de fecha (pesos ya en [unidad]). */
    val progreso: List<Double> = emptyList(),
    val unidad: UnidadPeso = UnidadPeso.KG,
)
