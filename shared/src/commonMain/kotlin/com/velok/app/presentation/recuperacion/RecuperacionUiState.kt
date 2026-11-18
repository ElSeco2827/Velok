package com.velok.app.presentation.recuperacion

import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.ErroresRecuperacion

data class RecuperacionUiState(
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errores: ErroresRecuperacion = ErroresRecuperacion(),
    /** Rechazo del servidor (correo no registrado). */
    val errorCuenta: ErrorCuenta? = null,
    val cargando: Boolean = false,
)
