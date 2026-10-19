package com.velok.app.presentation.editarplan

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Plan

data class EditarPlanUiState(
    val cargando: Boolean = true,
    /** null cuando terminó de cargar y el plan no existe. */
    val plan: Plan? = null,
    /** Ejercicios que se pueden agregar al plan. */
    val catalogo: List<Ejercicio> = emptyList(),
)
