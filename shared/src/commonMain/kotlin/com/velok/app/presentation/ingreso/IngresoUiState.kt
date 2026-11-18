package com.velok.app.presentation.ingreso

import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.ErroresIngreso

data class IngresoUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errores: ErroresIngreso = ErroresIngreso(),
    /** Rechazo del servidor (credenciales incorrectas). */
    val errorCuenta: ErrorCuenta? = null,
    val cargando: Boolean = false,
    val cargandoGoogle: Boolean = false,
)
