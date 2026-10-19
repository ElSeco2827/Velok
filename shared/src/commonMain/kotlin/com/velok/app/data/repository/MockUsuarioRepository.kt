package com.velok.app.data.repository

import com.velok.app.domain.model.Usuario
import com.velok.app.domain.repository.UsuarioRepository

/** Usuario fijo mientras no hay inicio de sesión. */
class MockUsuarioRepository : UsuarioRepository {
    override suspend fun obtenerUsuario(): Usuario = Usuario(nombre = "Checo")
}
