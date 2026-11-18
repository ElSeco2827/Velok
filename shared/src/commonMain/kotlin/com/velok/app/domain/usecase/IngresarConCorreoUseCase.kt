package com.velok.app.domain.usecase

import com.velok.app.domain.model.ErroresIngreso
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.ResultadoFormulario
import com.velok.app.domain.repository.AuthRepository
import com.velok.app.domain.validacion.ReglasCuenta

/** Inicio de sesión con correo: valida los campos y, si son correctos, consulta al repositorio. */
class IngresarConCorreoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(correo: String, contrasena: String): ResultadoFormulario<ErroresIngreso> {
        val errores = ErroresIngreso(
            correo = ReglasCuenta.validarCorreo(correo),
            contrasena = ReglasCuenta.validarContrasena(contrasena),
        )
        if (errores.hayErrores) return ResultadoFormulario.Invalido(errores)
        return when (val r = repository.ingresar(ReglasCuenta.normalizarCorreo(correo), contrasena)) {
            is ResultadoAuth.Exito -> ResultadoFormulario.Exito(r.usuario)
            is ResultadoAuth.Fallo -> ResultadoFormulario.Rechazado(r.error)
        }
    }
}
