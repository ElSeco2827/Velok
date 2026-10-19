package com.velok.app.domain.usecase

import com.velok.app.domain.calculo.calcularDistribucionMuscular
import com.velok.app.domain.model.ComparacionMuscular
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Series por grupo muscular en el rango, comparadas con el periodo anterior de la misma duración. */
class ObtenerComparacionMuscularUseCase(
    private val repository: SesionRepository,
    private val clock: Clock = Clock.System,
) {
    operator fun invoke(rango: RangoTiempo): Flow<ComparacionMuscular> = repository.observarHistorial().map { historial ->
        val zona = TimeZone.currentSystemDefault()
        val hoy = clock.todayIn(zona)
        val desde = hoy.minus(rango.dias - 1, DateTimeUnit.DAY)
        val desdeAnterior = desde.minus(rango.dias, DateTimeUnit.DAY)
        val hastaAnterior = desde.minus(1, DateTimeUnit.DAY)
        ComparacionMuscular(
            actual = calcularDistribucionMuscular(historial, desde, hoy, zona),
            anterior = calcularDistribucionMuscular(historial, desdeAnterior, hastaAnterior, zona),
        )
    }
}
