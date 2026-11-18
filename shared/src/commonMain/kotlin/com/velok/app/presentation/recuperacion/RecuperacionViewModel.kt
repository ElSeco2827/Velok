package com.velok.app.presentation.recuperacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.ResultadoFormulario
import com.velok.app.domain.model.destinoPara
import com.velok.app.domain.usecase.RestablecerContrasenaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** "Recuperación de contraseña". Al escribir en un campo se borra su error. */
class RecuperacionViewModel(private val restablecer: RestablecerContrasenaUseCase) : ViewModel() {

    private val _estado = MutableStateFlow(RecuperacionUiState())
    val estado: StateFlow<RecuperacionUiState> = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) = _estado.update {
        it.copy(correo = valor, errores = it.errores.copy(correo = null), errorCuenta = null)
    }

    fun cambiarContrasena(valor: String) = _estado.update {
        it.copy(contrasena = valor, errores = it.errores.copy(contrasena = null))
    }

    fun cambiarConfirmacion(valor: String) = _estado.update {
        it.copy(confirmacion = valor, errores = it.errores.copy(confirmacion = null))
    }

    fun continuar(onListo: (DestinoInicial) -> Unit) {
        val actual = _estado.value
        if (actual.cargando) return
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorCuenta = null) }
            when (val r = restablecer(actual.correo, actual.contrasena, actual.confirmacion)) {
                is ResultadoFormulario.Exito -> onListo(destinoPara(r.usuario))
                is ResultadoFormulario.Invalido -> _estado.update { it.copy(errores = r.errores) }
                is ResultadoFormulario.Rechazado -> _estado.update { it.copy(errorCuenta = r.error) }
            }
            _estado.update { it.copy(cargando = false) }
        }
    }
}
