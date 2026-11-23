package com.velok.app.domain.model

import kotlinx.datetime.LocalDate

/**
 * Meta diaria de pasos del anillo de "Actividad diaria". Es un valor de referencia común en apps
 * de actividad física, no una recomendación médica; el anillo muestra pasos del día / meta.
 */
const val META_PASOS_DIARIOS = 8_000

/** Si la app puede leer los datos de salud del dispositivo (Health Connect / HealthKit). */
enum class AccesoSalud {
    /** Hay permiso para leer al menos un tipo de dato. */
    DISPONIBLE,

    /** La fuente existe pero el usuario no ha dado permiso (o lo quitó). */
    SIN_PERMISO,

    /** El dispositivo no la soporta (por ejemplo, Android 8 o un iPad sin Salud). */
    NO_DISPONIBLE,

    /** Android 9 a 13: hay que instalar o actualizar la app Health Connect desde la tienda. */
    REQUIERE_INSTALAR,
}

/**
 * Valores de una hora del día (0 = de 00:00 a 00:59). null = sin datos en esa hora.
 */
data class MuestraHora(
    val hora: Int,
    val pasos: Long? = null,
    val distanciaMetros: Double? = null,
    val caloriasActivas: Double? = null,
    val ritmoPromedio: Double? = null,
)

/**
 * Datos de salud de un día tal como los da la plataforma, sin formato. null = sin datos (o sin
 * permiso para ese tipo): la pantalla muestra "--" en lugar de un 0 que parecería un valor real.
 */
data class ResumenSalud(
    val fecha: LocalDate,
    val pasos: Long? = null,
    val distanciaMetros: Double? = null,
    val caloriasActivas: Double? = null,
    /** Promedio de las mediciones del día, en latidos por minuto. */
    val ritmoPromedio: Double? = null,
    /** Solo las horas con algún dato, en cualquier orden. */
    val porHora: List<MuestraHora> = emptyList(),
)

/** Lo que muestra la tarjeta de actividad: los datos, o por qué no los hay. */
sealed interface ActividadSalud {
    data class Datos(val actividad: ActividadDiaria, val metricas: List<Metrica>) : ActividadSalud
    data object SinPermiso : ActividadSalud
    data object NoDisponible : ActividadSalud
    data object RequiereInstalar : ActividadSalud
}

/** Avance hacia la meta de pasos, de 0 a 1 (se queda en 1 aunque se pase la meta). */
fun progresoDelDia(pasos: Long?, meta: Int = META_PASOS_DIARIOS): Float {
    if (pasos == null || pasos <= 0 || meta <= 0) return 0f
    return (pasos.toFloat() / meta).coerceAtMost(1f)
}

/**
 * Convierte el resumen a las 4 métricas de la tarjeta, en sus unidades de pantalla (kcal, pasos,
 * km, lpm), con la mini gráfica por hora.
 *
 * Mini gráficas: pasos, distancia y calorías tienen un valor por hora desde las 00:00 hasta
 * [horaActual] (las horas sin datos cuentan como 0 porque no hubo actividad); el ritmo cardiaco
 * solo usa las horas con mediciones (una hora sin medición no significa 0 lpm).
 */
fun ResumenSalud.aMetricas(horaActual: Int): List<Metrica> {
    val horas = porHora.associateBy { it.hora }
    val hasta = horaActual.coerceIn(0, 23)
    fun serie(valor: (MuestraHora) -> Double?): List<Float> =
        (0..hasta).map { h -> horas[h]?.let(valor)?.toFloat() ?: 0f }

    return listOf(
        Metrica(TipoMetrica.CALORIAS, caloriasActivas, serie { it.caloriasActivas }),
        Metrica(TipoMetrica.PASOS, pasos?.toDouble(), serie { it.pasos?.toDouble() }),
        Metrica(TipoMetrica.DISTANCIA, distanciaMetros?.let { it / METROS_POR_KM }, serie { it.distanciaMetros?.let { m -> m / METROS_POR_KM } }),
        Metrica(
            TipoMetrica.RITMO_CARDIACO,
            ritmoPromedio,
            porHora.sortedBy { it.hora }.mapNotNull { it.ritmoPromedio?.toFloat() },
        ),
    )
}

/** La actividad del día para la tarjeta (fecha y anillo de pasos). */
fun ResumenSalud.aActividadDiaria(meta: Int = META_PASOS_DIARIOS): ActividadDiaria =
    ActividadDiaria(fecha = fecha, progreso = progresoDelDia(pasos, meta), pasos = pasos, metaPasos = meta)

private const val METROS_POR_KM = 1_000.0
