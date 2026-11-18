package com.velok.app.presentation.bienvenida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.destinoPara
import com.velok.app.domain.usecase.IngresarConGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BienvenidaViewModel(private val ingresarConGoogle: IngresarConGoogleUseCase) : ViewModel() {

    private val _estado = MutableStateFlow(BienvenidaUiState())
    val estado: StateFlow<BienvenidaUiState> = _estado.asStateFlow()

    fun continuarConGoogle(onListo: (DestinoInicial) -> Unit) {
        if (_estado.value.cargandoGoogle) return
        viewModelScope.launch {
            _estado.update { it.copy(cargandoGoogle = true, error = null) }
            when (val resultado = ingresarConGoogle()) {
                is ResultadoAuth.Exito -> onListo(destinoPara(resultado.usuario))
                is ResultadoAuth.Fallo -> _estado.update { it.copy(error = resultado.error) }
            }
            _estado.update { it.copy(cargandoGoogle = false) }
        }
    }
}
