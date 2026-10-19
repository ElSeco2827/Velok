package com.velok.app.domain.usecase

import com.velok.app.domain.model.Usuario
import com.velok.app.domain.repository.UsuarioRepository

class ObtenerUsuarioUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(): Usuario = repository.obtenerUsuario()
}
