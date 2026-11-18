package com.velok.app.presentation.ingreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.ResultadoFormulario
import com.velok.app.domain.model.destinoPara
import com.velok.app.domain.usecase.IngresarConCorreoUseCase
import com.velok.app.domain.usecase.IngresarConGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** "Inicio de sesión". Al escribir en un campo se borra su error. */
class IngresoViewModel(
    private val ingresarConCorreo: IngresarConCorreoUseCase,
    private val ingresarConGoogle: IngresarConGoogleUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(IngresoUiState())
    val estado: StateFlow<IngresoUiState> = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) = _estado.update {
        it.copy(correo = valor, errores = it.errores.copy(correo = null), errorCuenta = null)
    }

    fun cambiarContrasena(valor: String) = _estado.update {
        it.copy(contrasena = valor, errores = it.errores.copy(contrasena = null), errorCuenta = null)
    }

    fun ingresar(onListo: (DestinoInicial) -> Unit) {
        val actual = _estado.value
        if (actual.cargando || actual.cargandoGoogle) return
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorCuenta = null) }
            when (val r = ingresarConCorreo(actual.correo, actual.contrasena)) {
                is ResultadoFormulario.Exito -> onListo(destinoPara(r.usuario))
                is ResultadoFormulario.Invalido -> _estado.update { it.copy(errores = r.errores) }
                is ResultadoFormulario.Rechazado -> _estado.update { it.copy(errorCuenta = r.error) }
            }
            _estado.update { it.copy(cargando = false) }
        }
    }

    fun ingresarGoogle(onListo: (DestinoInicial) -> Unit) {
        val actual = _estado.value
        if (actual.cargando || actual.cargandoGoogle) return
        viewModelScope.launch {
            _estado.update { it.copy(cargandoGoogle = true, errorCuenta = null) }
            when (val r = ingresarConGoogle()) {
                is ResultadoAuth.Exito -> onListo(destinoPara(r.usuario))
                is ResultadoAuth.Fallo -> _estado.update { it.copy(errorCuenta = r.error) }
            }
            _estado.update { it.copy(cargandoGoogle = false) }
        }
    }
}
