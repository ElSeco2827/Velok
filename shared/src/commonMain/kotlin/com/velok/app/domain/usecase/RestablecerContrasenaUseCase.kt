package com.velok.app.domain.usecase

import com.velok.app.domain.model.ErroresRecuperacion
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.ResultadoFormulario
import com.velok.app.domain.repository.AuthRepository
import com.velok.app.domain.validacion.ReglasCuenta

/** Cambia la contraseña de una cuenta existente y deja la sesión iniciada. */
class RestablecerContrasenaUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        correo: String,
        contrasena: String,
        confirmacion: String,
    ): ResultadoFormulario<ErroresRecuperacion> {
        val errores = ErroresRecuperacion(
            correo = ReglasCuenta.validarCorreo(correo),
            contrasena = ReglasCuenta.validarContrasena(contrasena),
            confirmacion = ReglasCuenta.validarConfirmacion(contrasena, confirmacion),
        )
        if (errores.hayErrores) return ResultadoFormulario.Invalido(errores)
        return when (val r = repository.restablecerContrasena(ReglasCuenta.normalizarCorreo(correo), contrasena)) {
            is ResultadoAuth.Exito -> ResultadoFormulario.Exito(r.usuario)
            is ResultadoAuth.Fallo -> ResultadoFormulario.Rechazado(r.error)
        }
    }
}
