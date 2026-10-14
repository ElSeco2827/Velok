package com.velok.app.domain.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Plan

/** Fuente de datos de ejercicios (en el futuro: API de ExerciseDB). */
interface EjercicioRepository {
    suspend fun obtenerPlanes(): List<Plan>
    suspend fun obtenerCategorias(): List<CategoriaEjercicio>
}
