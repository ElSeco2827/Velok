package com.velok.app.domain.usecase

import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.aActividadDiaria
import com.velok.app.domain.model.aMetricas
import com.velok.app.domain.repository.ActividadRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Actividad de hoy para la tarjeta "Actividad diaria": revisa primero si hay acceso a los datos
 * de salud y solo entonces los lee. Sin acceso, devuelve el motivo (la pantalla ofrece conectar,
 * instalar o explica que no está disponible) en lugar de números inventados.
 */
class ObtenerActividadSaludUseCase(
    private val repository: ActividadRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(): ActividadSalud = when (repository.obtenerAcceso()) {
        AccesoSalud.SIN_PERMISO -> ActividadSalud.SinPermiso
        AccesoSalud.NO_DISPONIBLE -> ActividadSalud.NoDisponible
        AccesoSalud.REQUIERE_INSTALAR -> ActividadSalud.RequiereInstalar
        AccesoSalud.DISPONIBLE -> {
            val ahora = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val resumen = repository.obtenerResumen(ahora.date)
            ActividadSalud.Datos(resumen.aActividadDiaria(), resumen.aMetricas(horaActual = ahora.hour))
        }
    }
}
