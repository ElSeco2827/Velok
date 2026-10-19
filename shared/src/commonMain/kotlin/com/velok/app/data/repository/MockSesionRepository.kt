package com.velok.app.data.repository

import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.iniciarSesion
import com.velok.app.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Historial en memoria. Se inicia con sesiones generadas para las últimas semanas, para que las
 * gráficas de progreso tengan datos. La generación es determinista (semilla fija).
 */
class MockSesionRepository(clock: Clock = Clock.System) : SesionRepository {

    private val historial = MutableStateFlow(generarHistorial(clock))

    override fun observarHistorial(): Flow<List<SesionEntrenamiento>> = historial

    override suspend fun guardarSesion(sesion: SesionEntrenamiento) {
        historial.update { it + sesion }
    }

    private companion object {
        const val DIAS_DE_HISTORIAL = 42
        const val PROBABILIDAD_DE_ENTRENAR = 0.75
        const val INCREMENTO_KG_POR_SEMANA = 1.25
        const val VARIACION_KG = 2.5

        // Cada plan del mock se entrena el día de la semana de su nombre.
        val planPorDia = mapOf(
            DayOfWeek.MONDAY to "plan-lunes",
            DayOfWeek.TUESDAY to "plan-martes",
            DayOfWeek.WEDNESDAY to "plan-miercoles",
            DayOfWeek.SATURDAY to "plan-sabado",
        )

        fun generarHistorial(clock: Clock): List<SesionEntrenamiento> {
            val zona = TimeZone.currentSystemDefault()
            val hoy = clock.todayIn(zona)
            val aleatorio = Random(seed = 7)
            return (DIAS_DE_HISTORIAL downTo 1).mapNotNull { diasAtras ->
                val fecha = hoy.minus(diasAtras, DateTimeUnit.DAY)
                val plan = DatosMock.planes.firstOrNull { it.id == planPorDia[fecha.dayOfWeek] }
                    ?: return@mapNotNull null
                if (aleatorio.nextDouble() > PROBABILIDAD_DE_ENTRENAR) return@mapNotNull null

                // El peso sube poco a poco (en el pasado se levantaba menos que en el plan actual),
                // con días mejores y peores para que la gráfica no sea una línea recta.
                val semanasAtras = diasAtras / 7
                val variacion = aleatorio.nextInt(-1, 2) * VARIACION_KG
                val inicio = fecha.atTime(LocalTime(18, 0)).toInstant(zona)
                val sesion = plan.iniciarSesion(id = "historial-$diasAtras", inicio = inicio)
                sesion.copy(
                    fin = inicio + aleatorio.nextInt(30, 50).minutes,
                    ejercicios = sesion.ejercicios.map { ej ->
                        ej.copy(series = ej.series.map { serie ->
                            val peso = serie.pesoKg?.let {
                                (it - semanasAtras * INCREMENTO_KG_POR_SEMANA + variacion).coerceAtLeast(0.0)
                            }
                            serie.copy(pesoKg = peso, completada = true)
                        })
                    },
                )
            }
        }
    }
}
