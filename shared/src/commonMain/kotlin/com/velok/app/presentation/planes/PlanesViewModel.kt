package com.velok.app.presentation.planes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.CrearPlanUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlanesViewModel(
    obtenerPlanes: ObtenerPlanesUseCase,
    private val crearPlan: CrearPlanUseCase,
) : ViewModel() {

    val estado: StateFlow<PlanesUiState> = obtenerPlanes()
        .map { PlanesUiState(cargando = false, planes = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlanesUiState())

    /** Crea un plan vacío y avisa su id para abrir el editor. */
    fun crear(onCreado: (String) -> Unit) {
        viewModelScope.launch { onCreado(crearPlan().id) }
    }
}
