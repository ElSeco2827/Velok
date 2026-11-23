package com.velok.app.presentation.dashboard

import com.velok.app.domain.model.Plan
import com.velok.app.presentation.salud.EstadoTarjetaSalud

/**
 * @property salud la tarjeta de actividad tiene su propio estado: si los datos de salud fallan o
 *   no hay permiso, los planes se siguen mostrando.
 */
data class DashboardUiState(
    val salud: EstadoTarjetaSalud = EstadoTarjetaSalud(),
    val planes: List<Plan> = emptyList(),
)
