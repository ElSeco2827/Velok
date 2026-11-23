package com.velok.app.presentation.editarplan

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.UnidadPeso

data class EditarPlanUiState(
    val cargando: Boolean = true,
    /** null cuando terminó de cargar y el plan no existe. */
    val plan: Plan? = null,
    /** Ejercicios que se pueden agregar al plan (ya filtrados por [busqueda]). */
    val catalogo: List<Ejercicio> = emptyList(),
    /** Texto del buscador de la hoja "Agregar ejercicio". */
    val busqueda: String = "",
    /** Unidad en la que se muestran y escriben los pesos (en el plan siempre están en kg). */
    val unidad: UnidadPeso = UnidadPeso.KG,
)
