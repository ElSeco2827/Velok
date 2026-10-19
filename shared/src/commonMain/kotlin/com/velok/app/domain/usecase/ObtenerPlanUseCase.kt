package com.velok.app.domain.usecase

import com.velok.app.domain.model.Plan
import com.velok.app.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow

class ObtenerPlanUseCase(private val repository: PlanRepository) {
    operator fun invoke(id: String): Flow<Plan?> = repository.observarPlan(id)
}
