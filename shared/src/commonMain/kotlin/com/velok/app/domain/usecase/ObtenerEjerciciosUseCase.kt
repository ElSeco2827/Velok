package com.velok.app.domain.usecase

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.FiltroCatalogo
import com.velok.app.domain.model.normalizarBusqueda
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.flow.Flow

/**
 * Catálogo filtrado (búsqueda, equipo y/o categoría). Los ejercicios con equipo OTRO solo
 * aparecen sin filtro de equipo (no tienen chip propio). El texto se normaliza aquí (minúsculas,
 * sin acentos) para que todas las fuentes de datos busquen igual.
 */
class ObtenerEjerciciosUseCase(private val repository: EjercicioRepository) {
    operator fun invoke(filtro: FiltroCatalogo = FiltroCatalogo()): Flow<List<Ejercicio>> =
        repository.observarEjercicios(filtro.copy(texto = normalizarBusqueda(filtro.texto)))
}
