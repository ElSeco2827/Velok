package com.velok.app.presentation.ejercicio

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoEquipo

enum class PestanaEjercicio { EJERCICIO, ENTRENAMIENTO }

data class EjercicioUiState(
    val pestana: PestanaEjercicio = PestanaEjercicio.EJERCICIO,
    val cargando: Boolean = true,
    val error: Boolean = false,
    /** null = sin filtro (todos los equipos). */
    val equipo: TipoEquipo? = null,
    /** Texto del buscador tal como lo escribe el usuario. */
    val busqueda: String = "",
    val ejercicios: List<Ejercicio> = emptyList(),
    val categorias: List<CategoriaEjercicio> = emptyList(),
    /** No se pudo descargar el catálogo y solo hay ejercicios de ejemplo (aviso discreto). */
    val sinConexion: Boolean = false,
)
