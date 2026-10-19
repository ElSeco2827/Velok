package com.velok.app.domain

import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.agregarSerie
import com.velok.app.domain.model.alternarCompletada
import com.velok.app.domain.model.cambiarValores
import com.velok.app.domain.model.iniciarSesion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class SesionEntrenamientoTest {

    private val inicio = Instant.fromEpochSeconds(1_000_000)

    private fun sesion() = planDePrueba(
        ejercicioPlanDePrueba(
            series = arrayOf(
                Serie(1, pesoKg = 10.0, repeticiones = 12),
                Serie(2, pesoKg = 20.0, repeticiones = 10),
                Serie(3, TipoSerie.CALENTAMIENTO, pesoKg = null, repeticiones = null),
            ),
        ),
    ).iniciarSesion("s", inicio)

    @Test
    fun iniciarSesion_partirDelPlanSinNadaCompletado() {
        val sesion = sesion()
        val series = sesion.ejercicios.single().series

        assertEquals(Serie(1, pesoKg = 10.0, repeticiones = 12), series[0].anterior)
        assertEquals(listOf(10.0, 20.0, null), series.map { it.pesoKg })
        assertTrue(series.none { it.completada })
        assertEquals(0, sesion.seriesCompletadas)
        assertEquals("plan", sesion.planId)
    }

    @Test
    fun volumen_soloCuentaSeriesCompletadas() {
        val sesion = sesion().alternarCompletada(0, 2)
        assertEquals(200.0, sesion.volumenKg)
        assertEquals(1, sesion.seriesCompletadas)
    }

    @Test
    fun alternarCompletada_marcaYDesmarca() {
        val marcada = sesion().alternarCompletada(0, 1)
        assertTrue(marcada.ejercicios[0].series[0].completada)
        assertFalse(marcada.alternarCompletada(0, 1).ejercicios[0].series[0].completada)
    }

    @Test
    fun alternarCompletada_noCompletaUnaSerieSinRepeticiones() {
        val sesion = sesion().alternarCompletada(0, 3)
        assertFalse(sesion.ejercicios[0].series[2].completada)
    }

    @Test
    fun serieSinPeso_aportaCeroKgPeroSiCuenta() {
        val sesion = sesion().cambiarValores(0, 3, pesoKg = null, repeticiones = 15).alternarCompletada(0, 3)
        assertEquals(1, sesion.seriesCompletadas)
        assertEquals(0.0, sesion.volumenKg)
    }

    @Test
    fun agregarSerie_copiaValoresSinAnterior() {
        val nueva = sesion().agregarSerie(0).ejercicios[0].series.last()
        assertEquals(4, nueva.id)
        assertNull(nueva.anterior)
        assertFalse(nueva.completada)
    }

    @Test
    fun duracion_usaFinSiExisteYSiNoLaHoraActual() {
        val sesion = sesion()
        assertEquals(5.minutes, sesion.duracion(inicio + 5.minutes))
        assertEquals(40.minutes, sesion.copy(fin = inicio + 40.minutes).duracion(inicio + 90.minutes))
    }
}
