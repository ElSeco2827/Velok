package com.velok.app.domain.usecase

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.repository.EjercicioRepository

/** Catálogo filtrado por equipo y/o categoría; null en un filtro significa "todos". */
class ObtenerEjerciciosUseCase(private val repository: EjercicioRepository) {
    suspend operator fun invoke(
        equipo: TipoEquipo? = null,
        categoria: TipoCategoria? = null,
    ): List<Ejercicio> = repository.obtenerEjercicios().filter {
        (equipo == null || it.equipo == equipo) && (categoria == null || it.categoria == categoria)
    }
}
