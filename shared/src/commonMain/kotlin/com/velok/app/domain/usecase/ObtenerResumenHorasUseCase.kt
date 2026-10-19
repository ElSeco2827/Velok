package com.velok.app.domain.usecase

import com.velok.app.domain.calculo.calcularHorasPorSemana
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.ResumenHoras
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Horas entrenadas por semana en el rango y total de la semana actual. */
class ObtenerResumenHorasUseCase(
    private val repository: SesionRepository,
    private val clock: Clock = Clock.System,
) {
    operator fun invoke(rango: RangoTiempo): Flow<ResumenHoras> = repository.observarHistorial().map { historial ->
        val zona = TimeZone.currentSystemDefault()
        val hoy = clock.todayIn(zona)
        val semanas = calcularHorasPorSemana(historial, hoy.minus(rango.dias - 1, DateTimeUnit.DAY), hoy, zona)
        ResumenHoras(horasEstaSemana = semanas.lastOrNull()?.horas ?: 0.0, semanas = semanas)
    }
}
