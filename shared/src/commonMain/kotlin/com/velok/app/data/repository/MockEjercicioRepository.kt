package com.velok.app.data.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EstadoCatalogo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/** Catálogo de ejemplo en memoria, para pruebas y vistas previas (sin red). */
class MockEjercicioRepository : EjercicioRepository {

    override suspend fun obtenerCategorias(): List<CategoriaEjercicio> {
        delay(RETRASO_SIMULADO_MS)
        return TipoCategoria.entries.map { tipo ->
            CategoriaEjercicio(id = tipo.name.lowercase(), tipo = tipo, imagenUrl = DatosMock.imagen(tipo))
        }
    }

    override fun observarEjercicios(): Flow<List<Ejercicio>> = flow {
        delay(RETRASO_SIMULADO_MS)
        emit(DatosMock.ejercicios)
    }

    override fun observarEstadoCatalogo(): Flow<EstadoCatalogo> = flowOf(EstadoCatalogo.LISTO)

    override suspend fun obtenerEjercicio(id: String): Ejercicio? =
        DatosMock.ejercicios.firstOrNull { it.id == id }

    private companion object {
        const val RETRASO_SIMULADO_MS = 300L
    }
}
