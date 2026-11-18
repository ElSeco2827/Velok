package com.velok.app.domain.usecase

import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.destinoPara
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first

/** Decide la primera pantalla: bienvenida, cuestionario o pantallas principales. */
class ObtenerDestinoInicialUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(): DestinoInicial = destinoPara(repository.observarUsuarioActual().first())
}
