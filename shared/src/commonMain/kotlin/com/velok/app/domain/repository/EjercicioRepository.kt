package com.velok.app.domain.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio

/** Catálogo de ejercicios (en el futuro: API de ExerciseDB). */
interface EjercicioRepository {
    suspend fun obtenerCategorias(): List<CategoriaEjercicio>
    suspend fun obtenerEjercicios(): List<Ejercicio>
    suspend fun obtenerEjercicio(id: String): Ejercicio?
}
