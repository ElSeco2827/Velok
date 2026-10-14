package com.velok.app.data.repository

import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

/** Datos falsos mientras se integra Health Connect / HealthKit. */
class MockActividadRepository : ActividadRepository {

    override suspend fun obtenerActividadDiaria(fecha: LocalDate): ActividadDiaria {
        delay(RETRASO_SIMULADO_MS)
        return ActividadDiaria(fecha = fecha, progreso = 0.7f)
    }

    override suspend fun obtenerMetricas(fecha: LocalDate): List<Metrica> {
        delay(RETRASO_SIMULADO_MS)
        return listOf(
            Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f, 6f, 9f)),
            Metrica(TipoMetrica.PASOS, 2032.0, listOf(1f, 2f, 4f, 3f, 5f, 6f, 8f, 7f)),
            Metrica(TipoMetrica.DISTANCIA, 1.5, listOf(3f, 2f, 4f, 3.5f, 6f, 5f, 7f, 8f)),
            Metrica(TipoMetrica.RITMO_CARDIACO, 90.0, listOf(5f, 7f, 6f, 8f, 6f, 7f, 6.5f, 7.5f)),
        )
    }

    private companion object {
        const val RETRASO_SIMULADO_MS = 300L
    }
}
