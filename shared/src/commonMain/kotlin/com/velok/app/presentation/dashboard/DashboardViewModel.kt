package com.velok.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.ObtenerActividadDiariaUseCase
import com.velok.app.domain.usecase.ObtenerMetricasUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val obtenerActividadDiaria: ObtenerActividadDiariaUseCase,
    private val obtenerMetricas: ObtenerMetricasUseCase,
    obtenerPlanes: ObtenerPlanesUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(DashboardUiState())
    val estado: StateFlow<DashboardUiState> = _estado.asStateFlow()

    init {
        // Los planes se observan: si se editan o borran, el carrusel se actualiza solo.
        obtenerPlanes()
            .onEach { planes -> _estado.update { it.copy(planes = planes) } }
            .launchIn(viewModelScope)
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = false) }
            try {
                coroutineScope {
                    val actividad = async { obtenerActividadDiaria() }
                    val metricas = async { obtenerMetricas() }
                    _estado.update {
                        it.copy(cargando = false, actividad = actividad.await(), metricas = metricas.await())
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = true) }
            }
        }
    }
}
