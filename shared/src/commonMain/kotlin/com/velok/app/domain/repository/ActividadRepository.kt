package com.velok.app.domain.repository

import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.ResumenSalud
import kotlinx.datetime.LocalDate

/**
 * Datos de actividad física del dispositivo, solo lectura: Health Connect en Android y HealthKit
 * en iOS (cada plataforma tiene su implementación). La app nunca escribe datos de salud.
 */
interface ActividadRepository {
    /** Se revisa antes de cada lectura: el usuario puede quitar el permiso en cualquier momento. */
    suspend fun obtenerAcceso(): AccesoSalud

    /** Datos crudos del día; los tipos sin permiso o sin registros quedan en null. */
    suspend fun obtenerResumen(fecha: LocalDate): ResumenSalud
}
