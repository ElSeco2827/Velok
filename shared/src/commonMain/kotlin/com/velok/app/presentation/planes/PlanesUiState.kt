package com.velok.app.presentation.planes

import com.velok.app.domain.model.Plan

data class PlanesUiState(
    val cargando: Boolean = true,
    val planes: List<Plan> = emptyList(),
)
