package com.velok.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Consultas de cada tabla. Las que devuelven Flow vuelven a emitir solas cuando cambian las tablas
// que leen: así las pantallas se actualizan sin recargar.

@Dao
interface EjercicioDao {
    @Query("SELECT * FROM ejercicio ORDER BY orden")
    suspend fun todos(): List<EjercicioEntidad>

    @Query("SELECT * FROM ejercicio WHERE id = :id")
    suspend fun porId(id: String): EjercicioEntidad?

    @Query("SELECT COUNT(*) FROM ejercicio")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ejercicios: List<EjercicioEntidad>)
}

@Dao
abstract class PlanDao {
    @Transaction
    @Query("SELECT * FROM plan ORDER BY orden")
    abstract fun observarTodos(): Flow<List<PlanCompleto>>

    @Transaction
    @Query("SELECT * FROM plan WHERE id = :id")
    abstract fun observar(id: String): Flow<PlanCompleto?>

    @Query("SELECT orden FROM plan WHERE id = :id")
    abstract suspend fun ordenDe(id: String): Int?

    @Query("SELECT COALESCE(MAX(orden), -1) FROM plan")
    abstract suspend fun ordenMaximo(): Int

    @Query("SELECT COUNT(*) FROM plan")
    abstract suspend fun contar(): Int

    @Query("DELETE FROM plan WHERE id = :id")
    abstract suspend fun borrar(id: String)

    @Upsert
    protected abstract suspend fun guardarPlan(plan: PlanEntidad)

    @Query("DELETE FROM plan_ejercicio WHERE planId = :planId")
    protected abstract suspend fun borrarEjercicios(planId: String)

    @Insert
    protected abstract suspend fun insertarEjercicios(ejercicios: List<PlanEjercicioEntidad>)

    @Insert
    protected abstract suspend fun insertarSeries(series: List<SeriePlanEntidad>)

    /**
     * Guarda el plan completo en una transacción: actualiza la fila del plan y reemplaza sus
     * ejercicios y series (borrar los ejercicios borra también sus series, por la cascada).
     */
    @Transaction
    open suspend fun reemplazar(plan: PlanEntidad, ejercicios: List<PlanEjercicioEntidad>, series: List<SeriePlanEntidad>) {
        guardarPlan(plan)
        borrarEjercicios(plan.id)
        insertarEjercicios(ejercicios)
        insertarSeries(series)
    }
}

@Dao
abstract class SesionDao {
    @Transaction
    @Query("SELECT * FROM sesion ORDER BY inicioMs")
    abstract fun observarTodas(): Flow<List<SesionCompleta>>

    @Query("SELECT COUNT(*) FROM sesion")
    abstract suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertarSesion(sesion: SesionEntidad)

    @Insert
    protected abstract suspend fun insertarEjercicios(ejercicios: List<SesionEjercicioEntidad>)

    @Insert
    protected abstract suspend fun insertarSeries(series: List<SerieSesionEntidad>)

    @Query("DELETE FROM sesion_ejercicio WHERE sesionId = :sesionId")
    protected abstract suspend fun borrarEjercicios(sesionId: String)

    /** Guarda la sesión con sus ejercicios y series en una transacción. */
    @Transaction
    open suspend fun guardar(sesion: SesionEntidad, ejercicios: List<SesionEjercicioEntidad>, series: List<SerieSesionEntidad>) {
        insertarSesion(sesion)
        borrarEjercicios(sesion.id)
        insertarEjercicios(ejercicios)
        insertarSeries(series)
    }
}

@Dao
interface CuentaDao {
    @Query("SELECT * FROM cuenta WHERE correo = :correo")
    suspend fun porCorreo(correo: String): CuentaEntidad?

    @Query("SELECT * FROM cuenta WHERE id = :id")
    suspend fun porId(id: String): CuentaEntidad?

    @Query("SELECT COUNT(*) FROM cuenta")
    suspend fun contar(): Int

    /** Falla si el correo ya existe (índice único). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(cuenta: CuentaEntidad)

    @Update
    suspend fun actualizar(cuenta: CuentaEntidad)

    /** La cuenta con sesión iniciada; emite null cuando no hay sesión. */
    @Query("SELECT cuenta.* FROM cuenta INNER JOIN usuario_actual ON cuenta.id = usuario_actual.usuarioId")
    fun observarActual(): Flow<CuentaEntidad?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun fijarActual(actual: UsuarioActualEntidad)

    @Query("DELETE FROM usuario_actual")
    suspend fun limpiarActual()
}
