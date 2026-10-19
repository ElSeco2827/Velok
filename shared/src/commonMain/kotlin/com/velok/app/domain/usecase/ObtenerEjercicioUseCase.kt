package com.velok.app.domain.usecase

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.repository.EjercicioRepository

class ObtenerEjercicioUseCase(private val repository: EjercicioRepository) {
    suspend operator fun invoke(id: String): Ejercicio? = repository.obtenerEjercicio(id)
}
