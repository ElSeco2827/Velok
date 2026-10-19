package com.velok.app.domain.usecase

import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.iniciarSesion
import com.velok.app.domain.repository.PlanRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

/** Prepara una sesión a partir del plan. Devuelve null si el plan no existe. */
class IniciarSesionUseCase(
    private val repository: PlanRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(planId: String): SesionEntrenamiento? {
        val plan = repository.observarPlan(planId).first() ?: return null
        val ahora = clock.now()
        return plan.iniciarSesion(id = "sesion-${ahora.toEpochMilliseconds()}", inicio = ahora)
    }
}
