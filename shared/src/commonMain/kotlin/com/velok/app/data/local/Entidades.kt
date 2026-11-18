package com.velok.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tablas de la base de datos local. Son modelos de la capa de datos: los repositorios locales los
// convierten a modelos del dominio (Conversiones.kt), así el dominio no depende de Room.
// Los enums se guardan por su nombre (TipoCategoria.PECHO -> "PECHO") y las fechas como texto ISO
// (LocalDate) o milisegundos desde 1970 (Instant).

/** Catálogo de ejercicios. [orden] conserva el orden del catálogo de ejemplo. */
@Entity(tableName = "ejercicio")
data class EjercicioEntidad(
    @PrimaryKey val id: String,
    val orden: Int,
    val nombre: String,
    val categoria: String,
    val equipo: String,
    val musculoPrimario: String,
    /** Nombres separados por coma ("TRICEPS,HOMBROS"); vacío si no hay. */
    val musculosSecundarios: String,
    val seriesSugeridas: Int,
    val repeticionesSugeridas: Int,
)

// ---------------------------------------------------------------- planes

/** [orden] mantiene los planes en el orden en que se crearon. */
@Entity(tableName = "plan")
data class PlanEntidad(
    @PrimaryKey val id: String,
    val orden: Int,
    val nombre: String,
    val categoria: String,
    val fecha: String,
)

/** Ejercicio dentro de un plan. Se borra junto con su plan. */
@Entity(
    tableName = "plan_ejercicio",
    foreignKeys = [
        ForeignKey(PlanEntidad::class, ["id"], ["planId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EjercicioEntidad::class, ["id"], ["ejercicioId"]),
    ],
    indices = [Index("planId"), Index("ejercicioId")],
)
data class PlanEjercicioEntidad(
    /** "planId:posicion": un mismo ejercicio puede aparecer dos veces en un plan. */
    @PrimaryKey val id: String,
    val planId: String,
    val posicion: Int,
    val ejercicioId: String,
    val descansoSegundos: Int,
    val nota: String,
)

@Entity(
    tableName = "serie_plan",
    primaryKeys = ["planEjercicioId", "id"],
    foreignKeys = [ForeignKey(PlanEjercicioEntidad::class, ["id"], ["planEjercicioId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("planEjercicioId")],
)
data class SeriePlanEntidad(
    val planEjercicioId: String,
    val id: Int,
    val posicion: Int,
    val tipo: String,
    val pesoKg: Double?,
    val repeticiones: Int?,
)

// ---------------------------------------------------------------- sesiones de entrenamiento

/**
 * Sesión terminada. No tiene llave foránea al plan a propósito: si el plan se borra, el historial
 * (y las gráficas de progreso) se conservan.
 */
@Entity(tableName = "sesion")
data class SesionEntidad(
    @PrimaryKey val id: String,
    val planId: String,
    val nombrePlan: String,
    val inicioMs: Long,
    val finMs: Long?,
)

@Entity(
    tableName = "sesion_ejercicio",
    foreignKeys = [
        ForeignKey(SesionEntidad::class, ["id"], ["sesionId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EjercicioEntidad::class, ["id"], ["ejercicioId"]),
    ],
    indices = [Index("sesionId"), Index("ejercicioId")],
)
data class SesionEjercicioEntidad(
    /** "sesionId:posicion". */
    @PrimaryKey val id: String,
    val sesionId: String,
    val posicion: Int,
    val ejercicioId: String,
    val descansoSegundos: Int,
    val nota: String,
)

/** Serie hecha en una sesión. Las columnas anterior* guardan lo que decía el plan (pueden ser null). */
@Entity(
    tableName = "serie_sesion",
    primaryKeys = ["sesionEjercicioId", "id"],
    foreignKeys = [ForeignKey(SesionEjercicioEntidad::class, ["id"], ["sesionEjercicioId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sesionEjercicioId")],
)
data class SerieSesionEntidad(
    val sesionEjercicioId: String,
    val id: Int,
    val posicion: Int,
    val tipo: String,
    val pesoKg: Double?,
    val repeticiones: Int?,
    val completada: Boolean,
    val anteriorId: Int?,
    val anteriorTipo: String?,
    val anteriorPesoKg: Double?,
    val anteriorRepeticiones: Int?,
)

// ---------------------------------------------------------------- cuentas

/**
 * Cuenta de usuario con su perfil físico (columnas null hasta completar el cuestionario).
 * [hashContrasena] tiene el formato de HashContrasenas; vacío en cuentas de Google, que no tienen
 * contraseña propia.
 */
@Entity(tableName = "cuenta", indices = [Index("correo", unique = true)])
data class CuentaEntidad(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val hashContrasena: String,
    val fotoUrl: String?,
    val fechaNacimiento: String?,
    val unidadPeso: String?,
    val estaturaCm: Int?,
    val pesoKg: Double?,
    val meta: String?,
)

/** Una sola fila (clave = 0) con quién inició sesión, para que la sesión sobreviva al cerrar la app. */
@Entity(
    tableName = "usuario_actual",
    foreignKeys = [ForeignKey(CuentaEntidad::class, ["id"], ["usuarioId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("usuarioId")],
)
data class UsuarioActualEntidad(
    @PrimaryKey val clave: Int = 0,
    val usuarioId: String,
)
