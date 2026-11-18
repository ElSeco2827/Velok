package com.velok.app.data.repository

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.aDominio
import com.velok.app.data.local.aFilas
import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/** Historial de entrenamientos guardado en la base de datos local, del más antiguo al más reciente. */
class LocalSesionRepository(
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
) : SesionRepository {

    private val dao = baseDatos.sesionDao()

    override fun observarHistorial(): Flow<List<SesionEntrenamiento>> = flow {
        sembrador.asegurarDatos()
        emitAll(dao.observarTodas().map { sesiones -> sesiones.map { it.aDominio() } })
    }

    override suspend fun guardarSesion(sesion: SesionEntrenamiento) {
        sembrador.asegurarDatos()
        val filas = sesion.aFilas()
        dao.guardar(filas.sesion, filas.ejercicios, filas.series)
    }
}
