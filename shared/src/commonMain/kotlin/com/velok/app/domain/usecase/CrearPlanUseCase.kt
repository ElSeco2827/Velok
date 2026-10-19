package com.velok.app.domain.usecase

import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.PlanRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Crea un plan vacío con fecha de hoy y lo guarda. El nombre lo escribe el usuario en el editor. */
class CrearPlanUseCase(
    private val repository: PlanRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(): Plan {
        val plan = Plan(
            id = "plan-${clock.now().toEpochMilliseconds()}",
            nombre = "",
            categoria = TipoCategoria.FUERZA,
            imagenUrl = "",
            fecha = clock.todayIn(TimeZone.currentSystemDefault()),
            ejercicios = emptyList(),
        )
        repository.guardarPlan(plan)
        return plan
    }
}
