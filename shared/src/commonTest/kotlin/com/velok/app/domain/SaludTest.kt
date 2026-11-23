package com.velok.app.domain

import com.velok.app.data.repository.MockActividadRepository
import com.velok.app.domain.model.AccesoSalud
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.META_PASOS_DIARIOS
import com.velok.app.domain.model.MuestraHora
import com.velok.app.domain.model.ResumenSalud
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.domain.model.aActividadDiaria
import com.velok.app.domain.model.aMetricas
import com.velok.app.domain.model.progresoDelDia
import com.velok.app.domain.usecase.ObtenerActividadSaludUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class SaludTest {

    private val hoy = LocalDate(2026, 11, 22)

    @Test
    fun laMetaDiariaEsDe8000Pasos() {
        assertEquals(8_000, META_PASOS_DIARIOS)
    }

    @Test
    fun progresoDelDiaSobreLaMetaDePasos() {
        assertEquals(0.5f, progresoDelDia(4_000))
        assertEquals(0.25f, progresoDelDia(500, meta = 2_000))
        // Pasarse de la meta llena el anillo, pero no más.
        assertEquals(1f, progresoDelDia(12_000))
        // Sin datos o con datos inválidos, el anillo queda vacío (no se inventa un avance).
        assertEquals(0f, progresoDelDia(null))
        assertEquals(0f, progresoDelDia(-5))
        assertEquals(0f, progresoDelDia(100, meta = 0))
    }

    @Test
    fun convierteElResumenALasCuatroMetricasEnSusUnidades() {
        val resumen = ResumenSalud(
            fecha = hoy,
            pasos = 5_600,
            distanciaMetros = 4_150.0,
            caloriasActivas = 210.5,
            ritmoPromedio = 84.0,
        )
        val metricas = resumen.aMetricas(horaActual = 20).associateBy { it.tipo }
        assertEquals(listOf(TipoMetrica.CALORIAS, TipoMetrica.PASOS, TipoMetrica.DISTANCIA, TipoMetrica.RITMO_CARDIACO), resumen.aMetricas(20).map { it.tipo })
        assertEquals(210.5, metricas.getValue(TipoMetrica.CALORIAS).valor)
        assertEquals(5_600.0, metricas.getValue(TipoMetrica.PASOS).valor)
        assertEquals(4.15, metricas.getValue(TipoMetrica.DISTANCIA).valor)
        assertEquals(84.0, metricas.getValue(TipoMetrica.RITMO_CARDIACO).valor)
    }

    @Test
    fun sinDatosLaMetricaQuedaEnNullYNoEnCero() {
        val metricas = ResumenSalud(fecha = hoy, pasos = 1_200).aMetricas(horaActual = 10).associateBy { it.tipo }
        assertEquals(1_200.0, metricas.getValue(TipoMetrica.PASOS).valor)
        assertNull(metricas.getValue(TipoMetrica.RITMO_CARDIACO).valor)
        assertNull(metricas.getValue(TipoMetrica.DISTANCIA).valor)
        assertNull(metricas.getValue(TipoMetrica.CALORIAS).valor)
        assertEquals(emptyList(), metricas.getValue(TipoMetrica.RITMO_CARDIACO).historial)
    }

    @Test
    fun lasMiniGraficasTienenUnValorPorHoraHastaLaHoraActual() {
        val resumen = ResumenSalud(
            fecha = hoy,
            pasos = 1_500,
            ritmoPromedio = 80.0,
            porHora = listOf(
                MuestraHora(hora = 9, pasos = 1_000, distanciaMetros = 700.0, ritmoPromedio = 90.0),
                MuestraHora(hora = 7, pasos = 500, ritmoPromedio = 70.0),
                MuestraHora(hora = 8, pasos = 0),
            ),
        )
        val metricas = resumen.aMetricas(horaActual = 10).associateBy { it.tipo }

        // Pasos, distancia y calorías: de las 00:00 a las 10:00 (11 valores); sin datos = 0.
        val pasos = metricas.getValue(TipoMetrica.PASOS).historial
        assertEquals(11, pasos.size)
        assertEquals(500f, pasos[7])
        assertEquals(1_000f, pasos[9])
        assertEquals(0f, pasos[10])
        assertEquals(0.7f, metricas.getValue(TipoMetrica.DISTANCIA).historial[9])
        // Ritmo cardiaco: solo las horas con mediciones, en orden (una hora sin medir no es 0 lpm).
        assertEquals(listOf(70f, 90f), metricas.getValue(TipoMetrica.RITMO_CARDIACO).historial)
    }

    @Test
    fun laHoraActualSeAcotaAlDia() {
        val resumen = ResumenSalud(fecha = hoy, pasos = 10)
        assertEquals(24, resumen.aMetricas(horaActual = 30).first { it.tipo == TipoMetrica.PASOS }.historial.size)
        assertEquals(1, resumen.aMetricas(horaActual = -3).first { it.tipo == TipoMetrica.PASOS }.historial.size)
    }

    @Test
    fun laActividadDiariaLlevaLosPasosYLaMeta() {
        val actividad = ResumenSalud(fecha = hoy, pasos = 2_000).aActividadDiaria()
        assertEquals(hoy, actividad.fecha)
        assertEquals(0.25f, actividad.progreso)
        assertEquals(2_000, actividad.pasos)
        assertEquals(META_PASOS_DIARIOS, actividad.metaPasos)
    }

    @Test
    fun elCasoDeUsoSoloLeeLosDatosSiHayAcceso() = runTest {
        val reloj = object : Clock {
            override fun now(): Instant = LocalDateTime(2026, 11, 22, 19, 30).toInstant(TimeZone.currentSystemDefault())
        }
        val repositorio = MockActividadRepository(retrasoMs = 0)
        val obtener = ObtenerActividadSaludUseCase(repositorio, reloj)

        val datos = assertIs<ActividadSalud.Datos>(obtener())
        assertEquals(hoy, datos.actividad.fecha)
        assertEquals(0.7f, datos.actividad.progreso)
        assertEquals(20, datos.metricas.first { it.tipo == TipoMetrica.PASOS }.historial.size)

        repositorio.acceso = AccesoSalud.SIN_PERMISO
        assertEquals(ActividadSalud.SinPermiso, obtener())
        repositorio.acceso = AccesoSalud.NO_DISPONIBLE
        assertEquals(ActividadSalud.NoDisponible, obtener())
        repositorio.acceso = AccesoSalud.REQUIERE_INSTALAR
        assertEquals(ActividadSalud.RequiereInstalar, obtener())
    }
}
