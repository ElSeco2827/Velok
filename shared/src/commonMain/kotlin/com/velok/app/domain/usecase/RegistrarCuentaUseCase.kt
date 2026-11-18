package com.velok.app.domain.usecase

import com.velok.app.domain.model.ErroresRegistro
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.ResultadoFormulario
import com.velok.app.domain.repository.AuthRepository
import com.velok.app.domain.validacion.ReglasCuenta

/** Crea una cuenta con correo: valida los cuatro campos y luego registra. */
class RegistrarCuentaUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmacion: String,
    ): ResultadoFormulario<ErroresRegistro> {
        val errores = ErroresRegistro(
            nombre = ReglasCuenta.validarNombre(nombre),
            correo = ReglasCuenta.validarCorreo(correo),
            contrasena = ReglasCuenta.validarContrasena(contrasena),
            confirmacion = ReglasCuenta.validarConfirmacion(contrasena, confirmacion),
        )
        if (errores.hayErrores) return ResultadoFormulario.Invalido(errores)
        return when (val r = repository.registrar(nombre.trim(), ReglasCuenta.normalizarCorreo(correo), contrasena)) {
            is ResultadoAuth.Exito -> ResultadoFormulario.Exito(r.usuario)
            is ResultadoAuth.Fallo -> ResultadoFormulario.Rechazado(r.error)
        }
    }
}
