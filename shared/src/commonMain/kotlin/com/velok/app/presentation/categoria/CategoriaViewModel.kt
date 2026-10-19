package com.velok.app.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriaViewModel(
    categoria: TipoCategoria,
    private val obtenerEjercicios: ObtenerEjerciciosUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(CategoriaUiState(categoria))
    val estado: StateFlow<CategoriaUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = false) }
            try {
                val ejercicios = obtenerEjercicios(categoria = _estado.value.categoria)
                _estado.update { it.copy(cargando = false, ejercicios = ejercicios) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = true) }
            }
        }
    }
}
