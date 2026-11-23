package com.velok.app.data.salud

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.MuestraHora
import com.velok.app.domain.model.ResumenSalud
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/**
 * Datos de actividad desde Health Connect (código específico de Android; en iOS está
 * HealthKitActividadRepository). Solo lee: nunca escribe datos de salud.
 *
 * Usa agregados de Health Connect (suma de pasos, distancia y calorías; promedio de ritmo
 * cardiaco), que ya quitan duplicados entre apps según las prioridades del usuario. Solo pide los
 * tipos con permiso concedido; los demás quedan en null y la tarjeta muestra "--".
 */
class HealthConnectActividadRepository(private val contexto: Context) : ActividadRepository {

    private val cliente by lazy { HealthConnectClient.getOrCreate(contexto) }

    override suspend fun obtenerAcceso(): AccesoSalud = when (HealthConnectClient.getSdkStatus(contexto)) {
        HealthConnectClient.SDK_AVAILABLE ->
            if (permisosConcedidos().isNotEmpty()) AccesoSalud.DISPONIBLE else AccesoSalud.SIN_PERMISO
        // Android 9 a 13 sin la app Health Connect, o con una versión vieja.
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> AccesoSalud.REQUIERE_INSTALAR
        // Android 8 o un dispositivo sin soporte.
        else -> AccesoSalud.NO_DISPONIBLE
    }

    override suspend fun obtenerResumen(fecha: LocalDate): ResumenSalud {
        val concedidos = permisosConcedidos()
        val metricas = METRICAS_POR_PERMISO.filterKeys { it in concedidos }.values.toSet()
        if (metricas.isEmpty()) return ResumenSalud(fecha)

        val zona = ZoneId.systemDefault()
        val inicio = fecha.toJavaLocalDate().atStartOfDay(zona).toInstant()
        val fin = minOf(inicio.plus(Duration.ofDays(1)), Instant.now())
        val rango = TimeRangeFilter.between(inicio, fin)

        val total = cliente.aggregate(AggregateRequest(metrics = metricas, timeRangeFilter = rango))
        // Cubetas de 1 hora; Health Connect omite las horas sin datos.
        val porHora = cliente.aggregateGroupByDuration(
            AggregateGroupByDurationRequest(
                metrics = metricas,
                timeRangeFilter = rango,
                timeRangeSlicer = Duration.ofHours(1),
                dataOriginFilter = emptySet(),
            ),
        )
        return ResumenSalud(
            fecha = fecha,
            pasos = total[StepsRecord.COUNT_TOTAL],
            distanciaMetros = total[DistanceRecord.DISTANCE_TOTAL]?.inMeters,
            caloriasActivas = total[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories,
            ritmoPromedio = total[HeartRateRecord.BPM_AVG]?.toDouble(),
            porHora = porHora.map { cubeta -> cubeta.result.aMuestra(cubeta.startTime.atZone(zona).hour) },
        )
    }

    private suspend fun permisosConcedidos(): Set<String> =
        cliente.permissionController.getGrantedPermissions() intersect PERMISOS_HEALTH_CONNECT

    private fun AggregationResult.aMuestra(hora: Int) = MuestraHora(
        hora = hora,
        pasos = this[StepsRecord.COUNT_TOTAL],
        distanciaMetros = this[DistanceRecord.DISTANCE_TOTAL]?.inMeters,
        caloriasActivas = this[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories,
        ritmoPromedio = this[HeartRateRecord.BPM_AVG]?.toDouble(),
    )

    private companion object {
        /** Qué agregado se pide con cada permiso de lectura. */
        val METRICAS_POR_PERMISO: Map<String, AggregateMetric<*>> = mapOf(
            HealthPermission.getReadPermission(StepsRecord::class) to StepsRecord.COUNT_TOTAL,
            HealthPermission.getReadPermission(DistanceRecord::class) to DistanceRecord.DISTANCE_TOTAL,
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class) to ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
            HealthPermission.getReadPermission(HeartRateRecord::class) to HeartRateRecord.BPM_AVG,
        )
    }
}
