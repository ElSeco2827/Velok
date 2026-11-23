package com.velok.app.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.FiltroCatalogo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/** Ejercicios de una categoría; se actualiza solo cuando llegan ejercicios descargados. */
class CategoriaViewModel(
    categoria: TipoCategoria,
    private val obtenerEjercicios: ObtenerEjerciciosUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(CategoriaUiState(categoria))
    val estado: StateFlow<CategoriaUiState> = _estado.asStateFlow()

    private var observacion: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        observacion?.cancel()
        _estado.update { it.copy(cargando = true, error = false) }
        observacion = obtenerEjercicios(FiltroCatalogo(categoria = _estado.value.categoria))
            .onEach { ejercicios -> _estado.update { it.copy(cargando = false, ejercicios = ejercicios) } }
            .catch { _estado.update { it.copy(cargando = false, error = true) } }
            .launchIn(viewModelScope)
    }
}
