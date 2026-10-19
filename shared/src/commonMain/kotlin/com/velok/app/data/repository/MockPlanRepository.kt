package com.velok.app.data.repository

import com.velok.app.domain.model.Plan
import com.velok.app.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Planes en memoria: los cambios duran mientras la app está abierta. */
class MockPlanRepository : PlanRepository {

    private val planes = MutableStateFlow(DatosMock.planes)

    override fun observarPlanes(): Flow<List<Plan>> = planes

    override fun observarPlan(id: String): Flow<Plan?> = planes.map { lista -> lista.firstOrNull { it.id == id } }

    override suspend fun guardarPlan(plan: Plan) {
        planes.update { lista ->
            if (lista.any { it.id == plan.id }) lista.map { if (it.id == plan.id) plan else it } else lista + plan
        }
    }

    override suspend fun eliminarPlan(id: String) {
        planes.update { lista -> lista.filterNot { it.id == id } }
    }
}
