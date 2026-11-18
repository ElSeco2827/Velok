package com.velok.app.domain.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EstadoCatalogo
import kotlinx.coroutines.flow.Flow

/** Catálogo de ejercicios (guardado en el dispositivo y actualizado desde ExerciseDB). */
interface EjercicioRepository {
    suspend fun obtenerCategorias(): List<CategoriaEjercicio>

    /** Catálogo que se muestra al usuario; vuelve a emitir cuando llegan ejercicios descargados. */
    fun observarEjercicios(): Flow<List<Ejercicio>>

    fun observarEstadoCatalogo(): Flow<EstadoCatalogo>

    /** Cualquier ejercicio por id, también los que ya no se muestran en el catálogo. */
    suspend fun obtenerEjercicio(id: String): Ejercicio?
}
