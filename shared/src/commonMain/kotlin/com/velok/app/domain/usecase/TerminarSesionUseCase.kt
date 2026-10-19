package com.velok.app.domain.usecase

import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.repository.SesionRepository
import kotlin.time.Clock

/**
 * Cierra la sesión y la guarda en el historial. Una sesión sin series completadas no se guarda
 * (no aporta datos al progreso); en ese caso devuelve false.
 */
class TerminarSesionUseCase(
    private val repository: SesionRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(sesion: SesionEntrenamiento): Boolean {
        if (sesion.seriesCompletadas == 0) return false
        repository.guardarSesion(sesion.copy(fin = clock.now()))
        return true
    }
}
