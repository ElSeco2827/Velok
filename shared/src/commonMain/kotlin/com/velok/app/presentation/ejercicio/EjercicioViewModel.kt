package com.velok.app.presentation.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.usecase.ObtenerCategoriasUseCase
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EjercicioViewModel(
    private val obtenerCategorias: ObtenerCategoriasUseCase,
    private val obtenerEjercicios: ObtenerEjerciciosUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(EjercicioUiState())
    val estado: StateFlow<EjercicioUiState> = _estado.asStateFlow()

    private var carga: Job? = null

    init {
        cargar()
    }

    fun seleccionarPestana(pestana: PestanaEjercicio) {
        _estado.update { it.copy(pestana = pestana) }
    }

    /** Tocar el chip seleccionado lo desactiva y se vuelven a mostrar todos los ejercicios. */
    fun alternarEquipo(equipo: TipoEquipo) {
        val nuevo = if (_estado.value.equipo == equipo) null else equipo
        _estado.update { it.copy(equipo = nuevo) }
        cargar()
    }

    fun cargar() {
        carga?.cancel()
        carga = viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = false) }
            try {
                coroutineScope {
                    val categorias = async { obtenerCategorias() }
                    val ejercicios = async { obtenerEjercicios(equipo = _estado.value.equipo) }
                    _estado.update {
                        it.copy(cargando = false, categorias = categorias.await(), ejercicios = ejercicios.await())
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
