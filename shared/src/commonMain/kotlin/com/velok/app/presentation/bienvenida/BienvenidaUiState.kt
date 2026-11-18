package com.velok.app.presentation.bienvenida

import com.velok.app.domain.model.ErrorCuenta

data class BienvenidaUiState(
    val cargandoGoogle: Boolean = false,
    val error: ErrorCuenta? = null,
)
