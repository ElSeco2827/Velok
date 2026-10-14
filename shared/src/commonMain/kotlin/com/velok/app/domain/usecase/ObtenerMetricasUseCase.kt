package com.velok.app.domain.usecase

import com.velok.app.domain.model.Metrica
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class ObtenerMetricasUseCase(
    private val repository: ActividadRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(): List<Metrica> =
        repository.obtenerMetricas(clock.todayIn(TimeZone.currentSystemDefault()))
}
