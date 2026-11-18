package com.velok.app.presentation.carga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.usecase.ObtenerDestinoInicialUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CargaViewModel(obtenerDestinoInicial: ObtenerDestinoInicialUseCase) : ViewModel() {

    private val _estado = MutableStateFlow(CargaUiState())
    val estado: StateFlow<CargaUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val destino = async { obtenerDestinoInicial() }
            // El logo se muestra un momento aunque la decisión sea inmediata.
            delay(TIEMPO_MINIMO_MS)
            _estado.value = CargaUiState(destino.await())
        }
    }

    private companion object {
        const val TIEMPO_MINIMO_MS = 1_200L
    }
}
