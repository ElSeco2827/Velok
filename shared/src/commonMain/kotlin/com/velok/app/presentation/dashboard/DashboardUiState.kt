package com.velok.app.presentation.dashboard

import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.Plan

data class DashboardUiState(
    val cargando: Boolean = true,
    val error: Boolean = false,
    val actividad: ActividadDiaria? = null,
    val metricas: List<Metrica> = emptyList(),
    val planes: List<Plan> = emptyList(),
    val tieneNotificaciones: Boolean = true,
)
