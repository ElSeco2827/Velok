package com.velok.app.presentation.util

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatoTest {

    @Test
    fun formatearMiles_agrupaDeTresEnTres() {
        assertEquals("0", formatearMiles(0))
        assertEquals("160", formatearMiles(160))
        assertEquals("2,032", formatearMiles(2032))
        assertEquals("1,234,567", formatearMiles(1_234_567))
        assertEquals("-2,032", formatearMiles(-2032))
    }

    @Test
    fun formatearDecimal_redondeaYRellena() {
        assertEquals("1.5", formatearDecimal(1.5))
        assertEquals("2.0", formatearDecimal(2.0))
        assertEquals("1.3", formatearDecimal(1.26))
        assertEquals("1,000.05", formatearDecimal(1000.049, 2))
        assertEquals("3", formatearDecimal(2.6, 0))
    }
}
