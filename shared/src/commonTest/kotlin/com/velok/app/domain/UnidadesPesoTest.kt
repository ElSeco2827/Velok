package com.velok.app.domain

import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.pesoAKg
import com.velok.app.domain.model.pesoEnUnidad
import kotlin.test.Test
import kotlin.test.assertEquals

/** Regla de unidades: se guarda en kg y se convierte solo al mostrar o capturar. */
class UnidadesPesoTest {

    private val tolerancia = 1e-9

    @Test
    fun enKgNoCambiaNada() {
        assertEquals(61.5, pesoEnUnidad(61.5, UnidadPeso.KG))
        assertEquals(61.5, pesoAKg(61.5, UnidadPeso.KG))
    }

    @Test
    fun convierteKgALibras() {
        assertEquals(220.46226218, pesoEnUnidad(100.0, UnidadPeso.LB), 1e-8)
        assertEquals(45.359237, pesoAKg(100.0, UnidadPeso.LB), tolerancia)
    }

    @Test
    fun idaYVueltaSinPerderPrecision() {
        // Lo que se escribe en lb se guarda en kg sin redondear y vuelve a mostrarse igual.
        for (libras in listOf(135.0, 2.5, 45.0, 0.5, 315.25)) {
            assertEquals(libras, pesoEnUnidad(pesoAKg(libras, UnidadPeso.LB), UnidadPeso.LB), tolerancia)
        }
        // Y cambiar la unidad muchas veces no altera el valor guardado en kg.
        var kg = 61.2
        repeat(100) { kg = pesoAKg(pesoEnUnidad(kg, UnidadPeso.LB), UnidadPeso.LB) }
        assertEquals(61.2, kg, tolerancia)
    }

    @Test
    fun lasMetricasDePesoSeConviertenYLasRepeticionesNo() {
        assertEquals(pesoEnUnidad(80.0, UnidadPeso.LB), MetricaProgreso.PESO.enUnidad(80.0, UnidadPeso.LB))
        assertEquals(pesoEnUnidad(4_000.0, UnidadPeso.LB), MetricaProgreso.VOLUMEN.enUnidad(4_000.0, UnidadPeso.LB))
        assertEquals(36.0, MetricaProgreso.REPETICIONES.enUnidad(36.0, UnidadPeso.LB))
    }
}
