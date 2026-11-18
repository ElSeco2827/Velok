package com.velok.app.presentation.util

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class FechasTest {

    @Test
    fun diasEnMes_consideraBisiestos() {
        assertEquals(31, diasEnMes(2026, 1))
        assertEquals(28, diasEnMes(2026, 2))
        assertEquals(29, diasEnMes(2024, 2))
        assertEquals(28, diasEnMes(1900, 2))  // divisible entre 100 pero no entre 400
        assertEquals(30, diasEnMes(2026, 4))
        assertEquals(31, diasEnMes(2026, 12))
    }

    @Test
    fun fechaAjustada_recortaElDiaAlFinDeMes() {
        assertEquals(LocalDate(2026, 2, 28), fechaAjustada(2026, 2, 31))
        assertEquals(LocalDate(2024, 2, 29), fechaAjustada(2024, 2, 30))
        assertEquals(LocalDate(2004, 4, 25), fechaAjustada(2004, 4, 25))
    }
}
