package com.velok.app.data.salud

import org.koin.core.module.Module

/**
 * Código específico de plataforma (expect/actual): de dónde salen los datos de actividad.
 * Registra en Koin el ActividadRepository de cada plataforma:
 * - Android (androidMain): HealthConnectActividadRepository (Health Connect).
 * - iOS (iosMain): HealthKitActividadRepository (HealthKit).
 * MockActividadRepository queda solo para pruebas y vistas previas.
 */
expect fun moduloSalud(): Module
