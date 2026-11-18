package com.velok.app.domain.repository

import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/** Usuario con sesión iniciada y su perfil. */
interface UsuarioRepository {
    /** null mientras no haya sesión. */
    fun observarUsuarioActual(): Flow<Usuario?>
    suspend fun guardarPerfilFisico(perfil: PerfilFisico)
}
