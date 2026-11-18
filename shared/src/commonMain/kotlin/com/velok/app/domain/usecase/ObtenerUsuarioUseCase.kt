package com.velok.app.domain.usecase

import com.velok.app.domain.model.Usuario
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow

/** Usuario con sesión iniciada (null si no hay sesión). */
class ObtenerUsuarioUseCase(private val repository: UsuarioRepository) {
    operator fun invoke(): Flow<Usuario?> = repository.observarUsuarioActual()
}
