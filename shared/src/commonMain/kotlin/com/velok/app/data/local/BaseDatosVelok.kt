package com.velok.app.data.local

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** Nombre del archivo de la base de datos en el dispositivo. */
const val NOMBRE_BASE_DATOS = "velok.db"

/**
 * Base de datos local de Velok (Room). Al cambiar cualquier tabla hay que subir [version] y
 * escribir una migración; el esquema de cada versión se exporta a shared/schemas.
 *
 * Versiones:
 * - 1: planes, sesiones, catálogo de ejemplo y cuentas.
 * - 2: catálogo de ExerciseDB (columnas imagenUrl y origen en ejercicio, tabla
 *   sincronizacion_catalogo). Migración automática: solo agrega columnas con valor por defecto y
 *   una tabla nueva, así que planes, sesiones y cuentas no se tocan.
 */
@Database(
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    entities = [
        EjercicioEntidad::class,
        PlanEntidad::class,
        PlanEjercicioEntidad::class,
        SeriePlanEntidad::class,
        SesionEntidad::class,
        SesionEjercicioEntidad::class,
        SerieSesionEntidad::class,
        CuentaEntidad::class,
        UsuarioActualEntidad::class,
        SincronizacionCatalogoEntidad::class,
    ],
    version = 2,
)
@ConstructedBy(ConstructorBaseDatos::class)
abstract class BaseDatosVelok : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun planDao(): PlanDao
    abstract fun sesionDao(): SesionDao
    abstract fun cuentaDao(): CuentaDao
    abstract fun semillaDao(): SemillaDao
    abstract fun sincronizacionDao(): SincronizacionDao
}

/** Room genera la implementación (`actual`) para cada plataforma con KSP. */
@Suppress("KotlinNoActualForExpect")
expect object ConstructorBaseDatos : RoomDatabaseConstructor<BaseDatosVelok> {
    override fun initialize(): BaseDatosVelok
}

/**
 * Termina de configurar la base de datos con lo que es igual en ambas plataformas. El
 * [constructor] lo crea cada plataforma con su ruta (ver `constructorBaseDatos` en androidMain e
 * iosMain).
 */
fun crearBaseDatos(constructor: RoomDatabase.Builder<BaseDatosVelok>): BaseDatosVelok =
    constructor
        // SQLite incluido en la app: misma versión en Android e iOS.
        .setDriver(BundledSQLiteDriver())
        // Las consultas nunca corren en el hilo principal.
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
