package com.velok.app.domain.model

/**
 * Usuario con sesión iniciada.
 *
 * @property perfil datos del cuestionario inicial; null hasta que lo completa.
 */
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val fotoUrl: String? = null,
    val perfil: PerfilFisico? = null,
) {
    val perfilCompleto: Boolean get() = perfil != null
}
