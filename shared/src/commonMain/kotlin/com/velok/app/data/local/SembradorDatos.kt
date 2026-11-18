package com.velok.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.velok.app.data.local.seguridad.HashContrasenas
import com.velok.app.data.repository.DatosMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Clock

/**
 * Copia los datos de ejemplo (DatosMock) a la base de datos la primera vez que se abre la app:
 * catálogo de ejercicios, los 4 planes, el historial y la cuenta demo (con su contraseña en hash).
 *
 * La base se considera "nueva" si no tiene ejercicios: el catálogo nunca se borra, así que si el
 * usuario borra todos sus planes no vuelven a aparecer los de ejemplo.
 */
class SembradorDatos(
    private val baseDatos: BaseDatosVelok,
    private val hash: HashContrasenas,
    private val clock: Clock,
) {
    private val candado = Mutex()
    private var listo = false

    /** Los repositorios lo llaman antes de leer o escribir. Solo trabaja la primera vez. */
    suspend fun asegurarDatos() {
        if (listo) return
        candado.withLock {
            if (listo) return
            val dao = baseDatos.semillaDao()
            if (dao.contarEjercicios() == 0) {
                // El hash tarda a propósito (ver HashContrasenas.ITERACIONES): fuera del hilo principal.
                val hashDemo = withContext(Dispatchers.Default) { hash.generar(DatosMock.CONTRASENA_DEMO) }
                val planes = DatosMock.planes.mapIndexed { orden, plan -> plan.aFilas(orden) }
                val sesiones = DatosMock.historial(clock).map { it.aFilas() }
                dao.sembrar(
                    ejercicios = DatosMock.ejercicios.mapIndexed { orden, ej -> ej.aEntidad(orden) },
                    planes = planes.map { it.plan },
                    planEjercicios = planes.flatMap { it.ejercicios },
                    seriesPlan = planes.flatMap { it.series },
                    sesiones = sesiones.map { it.sesion },
                    sesionEjercicios = sesiones.flatMap { it.ejercicios },
                    seriesSesion = sesiones.flatMap { it.series },
                    cuenta = DatosMock.usuarioDemo.aEntidad(hashDemo),
                )
            }
            listo = true
        }
    }
}

/** Inserciones de la siembra inicial, todas en una sola transacción (o se guarda todo o nada). */
@Dao
abstract class SemillaDao {
    @androidx.room.Query("SELECT COUNT(*) FROM ejercicio")
    abstract suspend fun contarEjercicios(): Int

    @Insert protected abstract suspend fun insertarEjercicios(filas: List<EjercicioEntidad>)
    @Insert protected abstract suspend fun insertarPlanes(filas: List<PlanEntidad>)
    @Insert protected abstract suspend fun insertarPlanEjercicios(filas: List<PlanEjercicioEntidad>)
    @Insert protected abstract suspend fun insertarSeriesPlan(filas: List<SeriePlanEntidad>)
    @Insert protected abstract suspend fun insertarSesiones(filas: List<SesionEntidad>)
    @Insert protected abstract suspend fun insertarSesionEjercicios(filas: List<SesionEjercicioEntidad>)
    @Insert protected abstract suspend fun insertarSeriesSesion(filas: List<SerieSesionEntidad>)
    @Insert protected abstract suspend fun insertarCuenta(fila: CuentaEntidad)

    @Transaction
    open suspend fun sembrar(
        ejercicios: List<EjercicioEntidad>,
        planes: List<PlanEntidad>,
        planEjercicios: List<PlanEjercicioEntidad>,
        seriesPlan: List<SeriePlanEntidad>,
        sesiones: List<SesionEntidad>,
        sesionEjercicios: List<SesionEjercicioEntidad>,
        seriesSesion: List<SerieSesionEntidad>,
        cuenta: CuentaEntidad,
    ) {
        insertarEjercicios(ejercicios)
        insertarPlanes(planes)
        insertarPlanEjercicios(planEjercicios)
        insertarSeriesPlan(seriesPlan)
        insertarSesiones(sesiones)
        insertarSesionEjercicios(sesionEjercicios)
        insertarSeriesSesion(seriesSesion)
        insertarCuenta(cuenta)
    }
}
