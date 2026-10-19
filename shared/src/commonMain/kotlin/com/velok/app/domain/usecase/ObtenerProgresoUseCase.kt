package com.velok.app.domain.usecase

import com.velok.app.domain.calculo.calcularProgreso
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.PuntoProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Progreso de un ejercicio (o de todos si [ejercicioId] es null) en los últimos días del rango. */
class ObtenerProgresoUseCase(
    private val repository: SesionRepository,
    private val clock: Clock = Clock.System,
) {
    operator fun invoke(
        ejercicioId: String?,
        metrica: MetricaProgreso,
        rango: RangoTiempo,
    ): Flow<List<PuntoProgreso>> = repository.observarHistorial().map { historial ->
        val zona = TimeZone.currentSystemDefault()
        val hoy = clock.todayIn(zona)
        val desde = hoy.minus(rango.dias - 1, DateTimeUnit.DAY)
        calcularProgreso(historial, ejercicioId, metrica, desde, hoy, zona)
    }
}
