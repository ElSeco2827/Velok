package com.velok.app.presentation.registro

import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.ErroresRegistro

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errores: ErroresRegistro = ErroresRegistro(),
    /** Rechazo del servidor (correo ya registrado). */
    val errorCuenta: ErrorCuenta? = null,
    val cargando: Boolean = false,
)
