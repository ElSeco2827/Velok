package com.velok.app.presentation.ejercicio

import com.velok.app.domain.model.CategoriaEjercicio

enum class PestanaEjercicio { EJERCICIO, ENTRENAMIENTO }

data class EjercicioUiState(
    val pestana: PestanaEjercicio = PestanaEjercicio.EJERCICIO,
    val cargando: Boolean = true,
    val error: Boolean = false,
    val categorias: List<CategoriaEjercicio> = emptyList(),
)
