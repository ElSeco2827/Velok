package com.velok.app.data.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.delay

/** Datos falsos mientras se integra la API de ExerciseDB. */
class MockEjercicioRepository : EjercicioRepository {

    override suspend fun obtenerCategorias(): List<CategoriaEjercicio> {
        delay(RETRASO_SIMULADO_MS)
        return TipoCategoria.entries.map { tipo ->
            CategoriaEjercicio(id = tipo.name.lowercase(), tipo = tipo, imagenUrl = DatosMock.imagen(tipo))
        }
    }

    override suspend fun obtenerEjercicios(): List<Ejercicio> {
        delay(RETRASO_SIMULADO_MS)
        return DatosMock.ejercicios
    }

    override suspend fun obtenerEjercicio(id: String): Ejercicio? =
        DatosMock.ejercicios.firstOrNull { it.id == id }

    private companion object {
        const val RETRASO_SIMULADO_MS = 300L
    }
}
