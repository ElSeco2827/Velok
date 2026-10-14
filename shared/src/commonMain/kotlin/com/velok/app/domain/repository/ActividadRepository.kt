package com.velok.app.domain.repository

import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import kotlinx.datetime.LocalDate

/** Fuente de datos de actividad física (en el futuro: Health Connect / HealthKit). */
interface ActividadRepository {
    suspend fun obtenerActividadDiaria(fecha: LocalDate): ActividadDiaria
    suspend fun obtenerMetricas(fecha: LocalDate): List<Metrica>
}
