package com.velok.app.domain.repository

import com.velok.app.domain.model.Plan
import kotlinx.coroutines.flow.Flow

/** Planes del usuario. Es observable para que todas las pantallas vean los cambios al instante. */
interface PlanRepository {
    fun observarPlanes(): Flow<List<Plan>>
    fun observarPlan(id: String): Flow<Plan?>
    /** Crea o reemplaza el plan con el mismo id. */
    suspend fun guardarPlan(plan: Plan)
    suspend fun eliminarPlan(id: String)
}
