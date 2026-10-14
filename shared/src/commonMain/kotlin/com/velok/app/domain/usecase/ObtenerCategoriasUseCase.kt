package com.velok.app.domain.usecase

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.repository.EjercicioRepository

class ObtenerCategoriasUseCase(private val repository: EjercicioRepository) {
    suspend operator fun invoke(): List<CategoriaEjercicio> = repository.obtenerCategorias()
}
