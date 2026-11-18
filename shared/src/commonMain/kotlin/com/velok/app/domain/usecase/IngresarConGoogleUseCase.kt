package com.velok.app.domain.usecase

import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.repository.AuthRepository

/** Registro o inicio de sesión con Google (la misma acción para cuentas nuevas y existentes). */
class IngresarConGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): ResultadoAuth = repository.ingresarConGoogle()
}
