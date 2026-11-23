package com.velok.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.ObtenerActividadSaludUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import com.velok.app.presentation.salud.recargar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val obtenerActividadSalud: ObtenerActividadSaludUseCase,
    obtenerPlanes: ObtenerPlanesUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(DashboardUiState())
    val estado: StateFlow<DashboardUiState> = _estado.asStateFlow()

    private var carga: Job? = null

    init {
        // Los planes se observan: si se editan o borran, el carrusel se actualiza solo.
        obtenerPlanes()
            .onEach { planes -> _estado.update { it.copy(planes = planes) } }
            .launchIn(viewModelScope)
        cargarSalud()
    }

    /**
     * Lee los datos de salud (también al volver a la app). [trasSolicitarPermiso] = se acaba de
     * cerrar el diálogo de permisos; si sigue sin permiso, se muestra que el usuario lo rechazó.
     */
    fun cargarSalud(trasSolicitarPermiso: Boolean = false) {
        carga?.cancel()
        carga = viewModelScope.launch {
            val nuevo = _estado.value.salud.recargar(obtenerActividadSalud, trasSolicitarPermiso)
            _estado.update { it.copy(salud = nuevo) }
        }
    }
}
