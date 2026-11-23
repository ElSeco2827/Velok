package com.velok.app.presentation.progreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.usecase.ObtenerActividadDiariaUseCase
import com.velok.app.domain.usecase.ObtenerMetricasUseCase
import com.velok.app.domain.usecase.ObtenerProgresoUseCase
import com.velok.app.domain.usecase.ObtenerUnidadPesoUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val obtenerActividadDiaria: ObtenerActividadDiariaUseCase,
    private val obtenerMetricas: ObtenerMetricasUseCase,
    obtenerProgreso: ObtenerProgresoUseCase,
    obtenerUnidadPeso: ObtenerUnidadPesoUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(ProgresoUiState())
    val estado: StateFlow<ProgresoUiState> = _estado.asStateFlow()

    init {
        cargar()
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

    fun cambiarMetrica(metrica: MetricaProgreso) = _estado.update { it.copy(metrica = metrica) }
    fun cambiarRango(rango: RangoTiempo) = _estado.update { it.copy(rango = rango) }
}
