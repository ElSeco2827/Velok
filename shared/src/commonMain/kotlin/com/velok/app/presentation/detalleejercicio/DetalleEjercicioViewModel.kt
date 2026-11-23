package com.velok.app.presentation.detalleejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.usecase.ObtenerEjercicioUseCase
import com.velok.app.domain.usecase.ObtenerProgresoUseCase
import com.velok.app.domain.usecase.ObtenerUnidadPesoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleEjercicioViewModel(
    private val ejercicioId: String,
    obtenerEjercicio: ObtenerEjercicioUseCase,
    obtenerProgreso: ObtenerProgresoUseCase,
    obtenerUnidadPeso: ObtenerUnidadPesoUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(DetalleEjercicioUiState())
    val estado: StateFlow<DetalleEjercicioUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val ejercicio = obtenerEjercicio(ejercicioId)
            _estado.update { it.copy(cargando = false, ejercicio = ejercicio) }
        }
        // Cada vez que cambia la métrica, el periodo o la unidad se vuelve a calcular el progreso.
        // El dominio lo calcula en kg; aquí solo se convierte para mostrarlo.
        val seleccion = _estado.map { it.metrica to it.rango }.distinctUntilChanged()
        combine(
            seleccion.flatMapLatest { (metrica, rango) -> obtenerProgreso(ejercicioId, metrica, rango).map { metrica to it } },
            obtenerUnidadPeso(),
        ) { (metrica, puntos), unidad ->
            _estado.update {
                it.copy(unidad = unidad, progreso = puntos.map { p -> metrica.enUnidad(p.valor, unidad) })
            }
        }.launchIn(viewModelScope)
    }

    fun cambiarMetrica(metrica: MetricaProgreso) = _estado.update { it.copy(metrica = metrica) }
    fun cambiarRango(rango: RangoTiempo) = _estado.update { it.copy(rango = rango) }
}
