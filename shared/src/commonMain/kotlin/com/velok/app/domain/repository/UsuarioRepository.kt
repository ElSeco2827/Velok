package com.velok.app.domain.repository

import com.velok.app.domain.model.Usuario

interface UsuarioRepository {
    suspend fun obtenerUsuario(): Usuario
}
