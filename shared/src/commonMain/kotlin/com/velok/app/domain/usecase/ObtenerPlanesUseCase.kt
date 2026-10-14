package com.velok.app.domain.usecase

import com.velok.app.domain.model.Plan
import com.velok.app.domain.repository.EjercicioRepository

class ObtenerPlanesUseCase(private val repository: EjercicioRepository) {
    suspend operator fun invoke(): List<Plan> = repository.obtenerPlanes()
}
