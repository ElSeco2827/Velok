package com.velok.app.data.repository

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.aDominio
import com.velok.app.data.local.patronLike
import com.velok.app.data.remote.SincronizadorCatalogo
import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EstadoCatalogo
import com.velok.app.domain.model.FiltroCatalogo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.normalizarBusqueda
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Catálogo de ejercicios con la estrategia "primero local": las pantallas siempre leen de la base
 * de datos (funciona sin internet) y la primera vez que se usa el catálogo se inicia en segundo
 * plano la descarga de ExerciseDB ([SincronizadorCatalogo]). Cuando llegan ejercicios nuevos, Room
 * vuelve a emitir y las pantallas se actualizan solas.
 *
 * @param alcance vive lo mismo que la app: la descarga sigue aunque se cierre la pantalla.
 */
class LocalEjercicioRepository(
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
    private val sincronizador: SincronizadorCatalogo,
    private val alcance: CoroutineScope,
) : EjercicioRepository {

    private val dao = baseDatos.ejercicioDao()

    /** Las categorías son fijas (un enum del dominio); no necesitan tabla. */
    override suspend fun obtenerCategorias(): List<CategoriaEjercicio> =
        TipoCategoria.entries.map { tipo ->
            CategoriaEjercicio(id = tipo.name.lowercase(), tipo = tipo, imagenUrl = DatosMock.imagen(tipo))
        }

    /** El filtro se aplica en la consulta de SQLite (ver [com.velok.app.data.local.EjercicioDao.buscar]). */
    override fun observarEjercicios(filtro: FiltroCatalogo): Flow<List<Ejercicio>> = flow {
        sembrador.asegurarDatos()
        sincronizador.iniciarUnaVez(alcance)
        val consulta = dao.buscar(
            texto = patronLike(normalizarBusqueda(filtro.texto)),
            musculos = filtro.musculosTexto.map { it.name },
            categorias = filtro.categoriasTexto.map { it.name },
            equipo = filtro.equipo?.name,
            categoria = filtro.categoria?.name,
        )
        emitAll(consulta.map { lista -> lista.map { it.aDominio() } })
    }

    override fun observarEstadoCatalogo(): Flow<EstadoCatalogo> = flow {
        sincronizador.iniciarUnaVez(alcance)
        emitAll(sincronizador.estado.distinctUntilChanged())
    }

    override suspend fun obtenerEjercicio(id: String): Ejercicio? {
        sembrador.asegurarDatos()
        return dao.porId(id)?.aDominio()
    }
}
