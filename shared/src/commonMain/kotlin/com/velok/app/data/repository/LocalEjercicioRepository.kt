package com.velok.app.data.repository

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.aDominio
import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.EjercicioRepository

/**
 * Catálogo de ejercicios guardado en la base de datos local (los planes y sesiones lo
 * referencian). Cuando se integre ExerciseDB, este repositorio puede descargarlo y guardarlo aquí.
 */
class LocalEjercicioRepository(
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
) : EjercicioRepository {

    private val dao = baseDatos.ejercicioDao()

    /** Las categorías son fijas (un enum del dominio); no necesitan tabla. */
    override suspend fun obtenerCategorias(): List<CategoriaEjercicio> =
        TipoCategoria.entries.map { tipo ->
            CategoriaEjercicio(id = tipo.name.lowercase(), tipo = tipo, imagenUrl = DatosMock.imagen(tipo))
        }

    override suspend fun obtenerEjercicios(): List<Ejercicio> {
        sembrador.asegurarDatos()
        return dao.todos().map { it.aDominio() }
    }

    override suspend fun obtenerEjercicio(id: String): Ejercicio? {
        sembrador.asegurarDatos()
        return dao.porId(id)?.aDominio()
    }
}
