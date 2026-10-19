package com.velok.app.domain.usecase

import com.velok.app.domain.repository.PlanRepository

class EliminarPlanUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke(id: String) = repository.eliminarPlan(id)
}
