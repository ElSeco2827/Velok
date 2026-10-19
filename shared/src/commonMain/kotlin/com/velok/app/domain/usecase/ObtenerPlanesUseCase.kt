package com.velok.app.domain.usecase

import com.velok.app.domain.model.Plan
import com.velok.app.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow

class ObtenerPlanesUseCase(private val repository: PlanRepository) {
    operator fun invoke(): Flow<List<Plan>> = repository.observarPlanes()
}
