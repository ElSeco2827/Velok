package com.velok.app.domain.repository

import com.velok.app.domain.model.ResultadoAuth

/**
 * Autenticación (en el futuro: un backend propio o Firebase Auth, y Google Sign-In).
 * Recibe datos ya validados por el dominio; el correo llega normalizado.
 */
interface AuthRepository {
    suspend fun ingresar(correo: String, contrasena: String): ResultadoAuth
    suspend fun ingresarConGoogle(): ResultadoAuth
    suspend fun registrar(nombre: String, correo: String, contrasena: String): ResultadoAuth
    suspend fun restablecerContrasena(correo: String, nuevaContrasena: String): ResultadoAuth

    /** Termina la sesión: a partir de aquí el usuario actual es null. */
    suspend fun cerrarSesion()
}
