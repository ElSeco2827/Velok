package com.velok.app.data.salud

import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.MuestraHora
import com.velok.app.domain.model.ResumenSalud
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.HealthKit.HKAuthorizationRequestStatusUnnecessary
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifier
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierDistanceWalkingRunning
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKStatistics
import platform.HealthKit.HKStatisticsCollectionQuery
import platform.HealthKit.HKStatisticsOptionCumulativeSum
import platform.HealthKit.HKStatisticsOptionDiscreteAverage
import platform.HealthKit.HKStatisticsOptions
import platform.HealthKit.HKStatisticsQuery
import platform.HealthKit.HKUnit
import platform.HealthKit.countUnit
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.meterUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.HealthKit.unitDividedByUnit
import kotlin.coroutines.resume
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Tipos de HealthKit que lee la app (solo lectura) y el almacén compartido con la solicitud de
 * permisos (PermisosSalud.ios.kt).
 */
@OptIn(ExperimentalForeignApi::class)
internal object HealthKitVelok {
    val almacen: HKHealthStore by lazy { HKHealthStore() }

    fun tipo(id: HKQuantityTypeIdentifier): HKQuantityType = requireNotNull(HKQuantityType.quantityTypeForIdentifier(id))

    val pasos by lazy { tipo(HKQuantityTypeIdentifierStepCount) }
    val distancia by lazy { tipo(HKQuantityTypeIdentifierDistanceWalkingRunning) }
    val calorias by lazy { tipo(HKQuantityTypeIdentifierActiveEnergyBurned) }
    val ritmo by lazy { tipo(HKQuantityTypeIdentifierHeartRate) }

    /** Solo lectura: la app nunca pide permiso para escribir. */
    val tiposLectura: Set<HKObjectType> by lazy { setOf(pasos, distancia, calorias, ritmo) }
}

/**
 * Datos de actividad desde HealthKit (código específico de iOS; en Android está
 * HealthConnectActividadRepository). Solo lee: nunca escribe datos de salud.
 *
 * Limitación de Apple: por privacidad, HealthKit no le dice a la app si el usuario negó la
 * lectura. Solo se sabe si ya se pidió el permiso; si lo negó, las consultas vuelven vacías y la
 * tarjeta muestra "--".
 */
@OptIn(ExperimentalForeignApi::class)
class HealthKitActividadRepository : ActividadRepository {

    private val almacen get() = HealthKitVelok.almacen

    override suspend fun obtenerAcceso(): AccesoSalud {
        if (!HKHealthStore.isHealthDataAvailable()) return AccesoSalud.NO_DISPONIBLE
        val estado = suspendCancellableCoroutine { continuacion ->
            almacen.getRequestStatusForAuthorizationToShareTypes(emptySet<Any?>(), HealthKitVelok.tiposLectura) { estado, _ ->
                continuacion.resume(estado)
            }
        }
        // "Unnecessary" = ya se pidió (Apple no dice si el usuario aceptó o negó la lectura).
        return if (estado == HKAuthorizationRequestStatusUnnecessary) {
            AccesoSalud.DISPONIBLE
        } else {
            AccesoSalud.SIN_PERMISO
        }
    }

    override suspend fun obtenerResumen(fecha: LocalDate): ResumenSalud {
        val inicioDia = fecha.atStartOfDayIn(TimeZone.currentSystemDefault())
        val fin = minOf(inicioDia + 1.days, Clock.System.now())
        val desde = NSDate.dateWithTimeIntervalSince1970(inicioDia.epochSeconds.toDouble())
        val hasta = NSDate.dateWithTimeIntervalSince1970(fin.epochSeconds.toDouble())

        val unidadRitmo = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
        val pasos = PorHora(HealthKitVelok.pasos, HKStatisticsOptionCumulativeSum, HKUnit.countUnit())
        val distancia = PorHora(HealthKitVelok.distancia, HKStatisticsOptionCumulativeSum, HKUnit.meterUnit())
        val calorias = PorHora(HealthKitVelok.calorias, HKStatisticsOptionCumulativeSum, HKUnit.kilocalorieUnit())
        val ritmo = PorHora(HealthKitVelok.ritmo, HKStatisticsOptionDiscreteAverage, unidadRitmo)
        val series = listOf(pasos, distancia, calorias, ritmo).associateWith { porHora(it, desde, hasta) }

        val horas: List<Int> = series.values.flatMap { it.keys }.distinct().sorted()
        return ResumenSalud(
            fecha = fecha,
            pasos = total(pasos, desde, hasta)?.toLong(),
            distanciaMetros = total(distancia, desde, hasta),
            caloriasActivas = total(calorias, desde, hasta),
            ritmoPromedio = total(ritmo, desde, hasta),
            porHora = horas.map { h ->
                MuestraHora(
                    hora = h,
                    pasos = series.getValue(pasos)[h]?.toLong(),
                    distanciaMetros = series.getValue(distancia)[h],
                    caloriasActivas = series.getValue(calorias)[h],
                    ritmoPromedio = series.getValue(ritmo)[h],
                )
            },
        )
    }

    /** Qué tipo consultar, cómo combinar sus muestras (suma o promedio) y en qué unidad. */
    private class PorHora(val tipo: HKQuantityType, val opcion: HKStatisticsOptions, val unidad: HKUnit)

    private fun HKStatistics.valor(c: PorHora): Double? =
        (if (c.opcion == HKStatisticsOptionCumulativeSum) sumQuantity() else averageQuantity())?.doubleValueForUnit(c.unidad)

    /** Valor del día completo. Sin datos (o sin permiso) HealthKit responde con error: null. */
    private suspend fun total(c: PorHora, desde: NSDate, hasta: NSDate): Double? = suspendCancellableCoroutine { cont ->
        val filtro = HKQuery.predicateForSamplesWithStartDate(desde, hasta, HKQueryOptionStrictStartDate)
        val consulta = HKStatisticsQuery(c.tipo, filtro, c.opcion) { _, estadisticas, _ ->
            cont.resume(estadisticas?.valor(c))
        }
        almacen.executeQuery(consulta)
    }

    /** Valores por hora del día (hora local -> valor); solo las horas con datos. */
    private suspend fun porHora(c: PorHora, desde: NSDate, hasta: NSDate): Map<Int, Double> = suspendCancellableCoroutine { cont ->
        val filtro = HKQuery.predicateForSamplesWithStartDate(desde, hasta, HKQueryOptionStrictStartDate)
        val intervalo = NSDateComponents().apply { setHour(1) }
        val consulta = HKStatisticsCollectionQuery(c.tipo, filtro, c.opcion, desde, intervalo)
        consulta.initialResultsHandler = { _, coleccion, _ ->
            val valores = mutableMapOf<Int, Double>()
            coleccion?.enumerateStatisticsFromDate(desde, hasta) { estadisticas, _ ->
                val valor = estadisticas?.valor(c)
                if (estadisticas != null && valor != null) {
                    val hora = NSCalendar.currentCalendar.component(NSCalendarUnitHour, estadisticas.startDate).toInt()
                    valores[hora] = valor
                }
            }
            cont.resume(valores)
        }
        almacen.executeQuery(consulta)
    }
}
