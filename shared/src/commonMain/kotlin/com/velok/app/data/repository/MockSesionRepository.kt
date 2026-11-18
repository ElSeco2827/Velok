package com.velok.app.data.repository

import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

/**
 * Historial en memoria, para pruebas y vistas previas. Empieza con el historial de ejemplo de
 * [DatosMock.historial].
 */
class MockSesionRepository(clock: Clock = Clock.System) : SesionRepository {

    private val historial = MutableStateFlow(DatosMock.historial(clock))

    override fun observarHistorial(): Flow<List<SesionEntrenamiento>> = historial

    override suspend fun guardarSesion(sesion: SesionEntrenamiento) {
        historial.update { it + sesion }
    }
}
