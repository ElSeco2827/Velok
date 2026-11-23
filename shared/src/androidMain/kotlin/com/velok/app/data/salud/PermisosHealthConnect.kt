package com.velok.app.data.salud

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord

/**
 * Permisos de Health Connect que pide la app: SOLO LECTURA de los 4 tipos que muestra la tarjeta.
 * Deben coincidir con los <uses-permission> de androidApp/src/main/AndroidManifest.xml.
 */
val PERMISOS_HEALTH_CONNECT: Set<String> = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(HeartRateRecord::class),
)
