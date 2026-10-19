package com.velok.app.presentation.detalleplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.ObtenerPlanUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetallePlanViewModel(
    planId: String,
    obtenerPlan: ObtenerPlanUseCase,
) : ViewModel() {

    val estado: StateFlow<DetallePlanUiState> = obtenerPlan(planId)
        .map { DetallePlanUiState(cargando = false, plan = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetallePlanUiState())
}
