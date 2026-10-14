package com.velok.app.domain.usecase

import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class ObtenerActividadDiariaUseCase(
    private val repository: ActividadRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(): ActividadDiaria =
        repository.obtenerActividadDiaria(clock.todayIn(TimeZone.currentSystemDefault()))
}
