package com.velok.app.domain.usecase

import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.repository.UsuarioRepository
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasPerfil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Guarda el cuestionario inicial. Vuelve a validar todo (aunque la pantalla ya validó cada paso)
 * para que ninguna otra pantalla pueda guardar un perfil inválido. Devuelve el primer error o null.
 */
class GuardarPerfilFisicoUseCase(
    private val repository: UsuarioRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(perfil: PerfilFisico): ErrorCampo? {
        val hoy = clock.todayIn(TimeZone.currentSystemDefault())
        val error = ReglasPerfil.validarFechaNacimiento(perfil.fechaNacimiento, hoy)
            ?: ReglasPerfil.validarEstatura(perfil.estaturaCm)
            ?: ReglasPerfil.validarPeso(perfil.pesoKg)
        if (error == null) repository.guardarPerfilFisico(perfil)
        return error
    }
}
