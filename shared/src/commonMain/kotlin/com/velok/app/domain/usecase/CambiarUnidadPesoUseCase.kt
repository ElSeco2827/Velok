package com.velok.app.domain.usecase

import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first

/**
 * Cambia la unidad de peso del usuario actual. Solo cambia cómo se muestran los pesos: todos se
 * siguen guardando en kg, así que no hay nada que convertir en la base de datos.
 */
class CambiarUnidadPesoUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(unidad: UnidadPeso) {
        val perfil = repository.observarUsuarioActual().first()?.perfil ?: return
        if (perfil.unidadPeso != unidad) repository.guardarPerfilFisico(perfil.copy(unidadPeso = unidad))
    }
}
