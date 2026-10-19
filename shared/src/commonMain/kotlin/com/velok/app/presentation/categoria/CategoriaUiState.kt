package com.velok.app.presentation.categoria

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoCategoria

data class CategoriaUiState(
    val categoria: TipoCategoria,
    val cargando: Boolean = true,
    val error: Boolean = false,
    val ejercicios: List<Ejercicio> = emptyList(),
)
