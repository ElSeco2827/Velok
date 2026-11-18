package com.velok.app.domain.usecase

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Catálogo filtrado por equipo y/o categoría; null en un filtro significa "todos". Los ejercicios
 * con equipo OTRO solo aparecen sin filtro de equipo (no tienen chip propio).
 */
class ObtenerEjerciciosUseCase(private val repository: EjercicioRepository) {
    operator fun invoke(
        equipo: TipoEquipo? = null,
        categoria: TipoCategoria? = null,
    ): Flow<List<Ejercicio>> = repository.observarEjercicios().map { lista ->
        lista.filter { (equipo == null || it.equipo == equipo) && (categoria == null || it.categoria == categoria) }
    }
}
