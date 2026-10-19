package com.velok.app.domain.repository

import com.velok.app.domain.model.SesionEntrenamiento
import kotlinx.coroutines.flow.Flow

/** Historial de sesiones terminadas. De aquí salen todas las estadísticas de progreso. */
interface SesionRepository {
    fun observarHistorial(): Flow<List<SesionEntrenamiento>>
    suspend fun guardarSesion(sesion: SesionEntrenamiento)
}
