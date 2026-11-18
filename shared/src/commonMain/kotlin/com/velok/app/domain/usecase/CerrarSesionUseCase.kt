package com.velok.app.domain.usecase

import com.velok.app.domain.repository.AuthRepository

/** Cierra la sesión del usuario. Sus planes y entrenamientos se conservan en el dispositivo. */
class CerrarSesionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.cerrarSesion()
}
