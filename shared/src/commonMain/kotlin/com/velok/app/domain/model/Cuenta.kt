package com.velok.app.domain.model

import com.velok.app.domain.validacion.ErrorCampo

/** Motivos por los que el servidor (hoy, el mock) rechaza una operación de cuenta. */
enum class ErrorCuenta {
    CREDENCIALES_INCORRECTAS,
    CORREO_YA_REGISTRADO,
    CORREO_NO_REGISTRADO,
}

/** Respuesta del repositorio de cuentas. */
sealed interface ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth
    data class Fallo(val error: ErrorCuenta) : ResultadoAuth
}

/**
 * Resultado de enviar un formulario de cuenta: primero se validan los campos (reglas del dominio)
 * y solo si son válidos se consulta al repositorio.
 */
sealed interface ResultadoFormulario<out E> {
    data class Exito(val usuario: Usuario) : ResultadoFormulario<Nothing>
    data class Invalido<E>(val errores: E) : ResultadoFormulario<E>
    data class Rechazado(val error: ErrorCuenta) : ResultadoFormulario<Nothing>
}

data class ErroresIngreso(val correo: ErrorCampo? = null, val contrasena: ErrorCampo? = null) {
    val hayErrores: Boolean get() = correo != null || contrasena != null
}

data class ErroresRegistro(
    val nombre: ErrorCampo? = null,
    val correo: ErrorCampo? = null,
    val contrasena: ErrorCampo? = null,
    val confirmacion: ErrorCampo? = null,
) {
    val hayErrores: Boolean get() = listOf(nombre, correo, contrasena, confirmacion).any { it != null }
}

data class ErroresRecuperacion(
    val correo: ErrorCampo? = null,
    val contrasena: ErrorCampo? = null,
    val confirmacion: ErrorCampo? = null,
) {
    val hayErrores: Boolean get() = listOf(correo, contrasena, confirmacion).any { it != null }
}

/** A dónde mandar al usuario al abrir la app. */
enum class DestinoInicial {
    /** Sin sesión: pantalla de selección de inicio. */
    BIENVENIDA,
    /** Con sesión pero sin cuestionario inicial. */
    FORMULARIO,
    /** Todo listo: pantallas principales. */
    PRINCIPAL,
}

/** Regla: el destino depende de si hay sesión y de si el perfil está completo. */
fun destinoPara(usuario: Usuario?): DestinoInicial = when {
    usuario == null -> DestinoInicial.BIENVENIDA
    !usuario.perfilCompleto -> DestinoInicial.FORMULARIO
    else -> DestinoInicial.PRINCIPAL
}
