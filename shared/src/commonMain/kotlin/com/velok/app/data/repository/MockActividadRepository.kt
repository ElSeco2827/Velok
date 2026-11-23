package com.velok.app.data.repository

import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.MuestraHora
import com.velok.app.domain.model.ResumenSalud
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

/**
 * Datos de salud de ejemplo, solo para pruebas y vistas previas. La app usa Health Connect
 * (Android) o HealthKit (iOS).
 *
 * @param acceso lo que devuelve [obtenerAcceso], para probar cada estado de la tarjeta.
 */
class MockActividadRepository(
    var acceso: AccesoSalud = AccesoSalud.DISPONIBLE,
    private val retrasoMs: Long = RETRASO_SIMULADO_MS,
) : ActividadRepository {

    override suspend fun obtenerAcceso(): AccesoSalud {
        delay(retrasoMs)
        return acceso
    }

    override suspend fun obtenerResumen(fecha: LocalDate): ResumenSalud {
        delay(retrasoMs)
        return ResumenSalud(
            fecha = fecha,
            pasos = 5_600,
            distanciaMetros = 4_100.0,
            caloriasActivas = 210.0,
            ritmoPromedio = 84.0,
            porHora = listOf(
                MuestraHora(7, pasos = 900, distanciaMetros = 650.0, caloriasActivas = 30.0, ritmoPromedio = 78.0),
                MuestraHora(9, pasos = 1_200, distanciaMetros = 900.0, caloriasActivas = 45.0, ritmoPromedio = 88.0),
                MuestraHora(12, pasos = 700, distanciaMetros = 500.0, caloriasActivas = 25.0),
                MuestraHora(18, pasos = 2_800, distanciaMetros = 2_050.0, caloriasActivas = 110.0, ritmoPromedio = 96.0),
            ),
        )
    }

    private companion object {
        const val RETRASO_SIMULADO_MS = 300L
    }
}
