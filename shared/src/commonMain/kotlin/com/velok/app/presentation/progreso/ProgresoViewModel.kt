package com.velok.app.presentation.progreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.usecase.ObtenerActividadSaludUseCase
import com.velok.app.domain.usecase.ObtenerProgresoUseCase
import com.velok.app.domain.usecase.ObtenerUnidadPesoUseCase
import com.velok.app.presentation.salud.recargar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Pestaña Actividad: actividad del día y progreso general de todas las sesiones. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProgresoViewModel(
    private val obtenerActividadSalud: ObtenerActividadSaludUseCase,
    obtenerProgreso: ObtenerProgresoUseCase,
    obtenerUnidadPeso: ObtenerUnidadPesoUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(ProgresoUiState())
    val estado: StateFlow<ProgresoUiState> = _estado.asStateFlow()

    private var carga: Job? = null

    init {
        cargarSalud()
        // El dominio calcula el progreso en kg; aquí solo se convierte a la unidad del usuario.
        val seleccion = _estado.map { it.metrica to it.rango }.distinctUntilChanged()
        combine(
            seleccion.flatMapLatest { (metrica, rango) ->
                obtenerProgreso(ejercicioId = null, metrica, rango).map { metrica to it }
            },
            obtenerUnidadPeso(),
        ) { (metrica, puntos), unidad ->
            _estado.update {
                it.copy(unidad = unidad, progreso = puntos.map { p -> metrica.enUnidad(p.valor, unidad) })
            }
        }.launchIn(viewModelScope)
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

    fun cambiarMetrica(metrica: MetricaProgreso) = _estado.update { it.copy(metrica = metrica) }
    fun cambiarRango(rango: RangoTiempo) = _estado.update { it.copy(rango = rango) }
}
