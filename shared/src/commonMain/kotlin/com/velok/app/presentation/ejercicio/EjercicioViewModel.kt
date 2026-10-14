package com.velok.app.presentation.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.ObtenerCategoriasUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EjercicioViewModel(
    private val obtenerCategorias: ObtenerCategoriasUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(EjercicioUiState())
    val estado: StateFlow<EjercicioUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun seleccionarPestana(pestana: PestanaEjercicio) {
        _estado.update { it.copy(pestana = pestana) }
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = false) }
            try {
                val categorias = obtenerCategorias()
                _estado.update { it.copy(cargando = false, categorias = categorias) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = true) }
            }
        }
    }
}
