package com.velok.app.domain.usecase

import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Unidad en la que el usuario ve y escribe los pesos. Vuelve a emitir cuando la cambia, así todas
 * las pantallas se actualizan al instante. Sin perfil, kg.
 */
class ObtenerUnidadPesoUseCase(private val repository: UsuarioRepository) {
    operator fun invoke(): Flow<UnidadPeso> =
        repository.observarUsuarioActual()
            .map { it?.perfil?.unidadPeso ?: UnidadPeso.KG }
            .distinctUntilChanged()
}
