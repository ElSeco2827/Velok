package com.velok.app.domain

import com.velok.app.domain.calculo.calcularDistribucionMuscular
import com.velok.app.domain.calculo.calcularHorasPorSemana
import com.velok.app.domain.calculo.calcularProgreso
import com.velok.app.domain.calculo.inicioDeSemana
import com.velok.app.domain.model.GrupoMuscular
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.PuntoProgreso
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.alternarCompletada
import com.velok.app.domain.model.iniciarSesion
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class EstadisticasTest {

    private val zona = TimeZone.UTC
    private val sentadilla = ejercicioDePrueba("sentadilla", TipoCategoria.PIERNA, Musculo.CUADRICEPS)
    private val press = ejercicioDePrueba("press", TipoCategoria.PECHO, Musculo.PECHO)

    /** Sesión terminada el [dia] de octubre de 2026 con [minutos] de duración. */
    private fun sesion(
        dia: Int,
        pesoSentadilla: Double,
        minutos: Int = 60,
        completarPress: Boolean = true,
    ): SesionEntrenamiento {
        val inicio = LocalDate(2026, 10, dia).atTime(LocalTime(18, 0)).toInstant(zona)
        var sesion = planDePrueba(
            ejercicioPlanDePrueba(
                sentadilla,
                Serie(1, pesoKg = pesoSentadilla - 10, repeticiones = 12),
                Serie(2, pesoKg = pesoSentadilla, repeticiones = 10),
            ),
            ejercicioPlanDePrueba(press, Serie(1, pesoKg = 30.0, repeticiones = 8)),
        ).iniciarSesion("s$dia", inicio)
        sesion = sesion.alternarCompletada(0, 1).alternarCompletada(0, 2)
        if (completarPress) sesion = sesion.alternarCompletada(1, 1)
        return sesion.copy(fin = inicio + minutos.minutes)
    }

    @Test
    fun inicioDeSemana_esElLunes() {
        // El 14 de octubre de 2026 es miércoles.
        assertEquals(LocalDate(2026, 10, 12), inicioDeSemana(LocalDate(2026, 10, 14)))
        assertEquals(LocalDate(2026, 10, 12), inicioDeSemana(LocalDate(2026, 10, 12)))
        assertEquals(LocalDate(2026, 10, 12), inicioDeSemana(LocalDate(2026, 10, 18)))
    }

    @Test
    fun progresoPeso_esElMaximoPorSesionDelEjercicio() {
        val historial = listOf(sesion(14, 50.0), sesion(12, 40.0))

        val puntos = calcularProgreso(
            historial, "sentadilla", MetricaProgreso.PESO,
            LocalDate(2026, 10, 1), LocalDate(2026, 10, 31), zona,
        )
        assertEquals(
            listOf(PuntoProgreso(LocalDate(2026, 10, 12), 40.0), PuntoProgreso(LocalDate(2026, 10, 14), 50.0)),
            puntos,
        )
    }

    @Test
    fun progresoVolumenYRepeticiones_sumanTodosLosEjerciciosSinFiltro() {
        // Sentadilla: 30×12 + 40×10 = 760; press: 30×8 = 240.
        val historial = listOf(sesion(12, 40.0))
        val desde = LocalDate(2026, 10, 12)

        assertEquals(1000.0, calcularProgreso(historial, null, MetricaProgreso.VOLUMEN, desde, desde, zona).single().valor)
        assertEquals(30.0, calcularProgreso(historial, null, MetricaProgreso.REPETICIONES, desde, desde, zona).single().valor)
    }

    @Test
    fun progreso_ignoraSesionesFueraDelRangoOSinDatosDelEjercicio() {
        val historial = listOf(sesion(5, 30.0), sesion(12, 40.0, completarPress = false))

        val puntos = calcularProgreso(
            historial, "press", MetricaProgreso.PESO,
            LocalDate(2026, 10, 10), LocalDate(2026, 10, 31), zona,
        )
        assertEquals(emptyList(), puntos)
    }

    @Test
    fun horasPorSemana_incluyeSemanasVaciasEIgnoraSesionesAbiertas() {
        val historial = listOf(
            sesion(12, 40.0, minutos = 60),
            sesion(14, 40.0, minutos = 30),
            sesion(15, 40.0).copy(fin = null),
        )

        val semanas = calcularHorasPorSemana(historial, LocalDate(2026, 9, 30), LocalDate(2026, 10, 14), zona)

        assertEquals(
            listOf(LocalDate(2026, 9, 28), LocalDate(2026, 10, 5), LocalDate(2026, 10, 12)),
            semanas.map { it.inicioSemana },
        )
        assertEquals(listOf(0.0, 0.0, 1.5), semanas.map { it.horas })
    }

    @Test
    fun distribucionMuscular_cuentaSeriesCompletadasPorGrupoPrimario() {
        val historial = listOf(sesion(12, 40.0), sesion(14, 40.0, completarPress = false))

        val conteo = calcularDistribucionMuscular(historial, LocalDate(2026, 10, 1), LocalDate(2026, 10, 31), zona)

        assertEquals(4, conteo[GrupoMuscular.PIERNAS])
        assertEquals(1, conteo[GrupoMuscular.PECHO])
        assertEquals(0, conteo[GrupoMuscular.ESPALDA])
        assertEquals(GrupoMuscular.entries.toSet(), conteo.keys)
    }
}
