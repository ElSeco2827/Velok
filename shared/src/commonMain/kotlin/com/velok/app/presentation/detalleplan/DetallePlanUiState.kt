package com.velok.app.presentation.detalleplan

import com.velok.app.domain.model.Plan

data class DetallePlanUiState(
    val cargando: Boolean = true,
    /** null cuando terminó de cargar y el plan no existe (por ejemplo, se borró). */
    val plan: Plan? = null,
)
