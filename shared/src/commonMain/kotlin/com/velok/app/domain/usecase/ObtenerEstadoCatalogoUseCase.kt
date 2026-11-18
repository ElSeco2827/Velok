package com.velok.app.domain.usecase

import com.velok.app.domain.model.EstadoCatalogo
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.flow.Flow

/** Estado de la descarga del catálogo (para el aviso de "sin conexión"). */
class ObtenerEstadoCatalogoUseCase(private val repository: EjercicioRepository) {
    operator fun invoke(): Flow<EstadoCatalogo> = repository.observarEstadoCatalogo()
}
