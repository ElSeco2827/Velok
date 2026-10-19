package com.velok.app.domain.usecase

import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.conCategoriaInferida
import com.velok.app.domain.repository.PlanRepository

/** Guarda el plan aplicando las reglas: nombre sin espacios sobrantes y categoría según sus ejercicios. */
class GuardarPlanUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan) {
        repository.guardarPlan(plan.copy(nombre = plan.nombre.trim()).conCategoriaInferida())
    }
}
