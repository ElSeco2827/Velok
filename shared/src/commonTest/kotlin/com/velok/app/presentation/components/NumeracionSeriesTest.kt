package com.velok.app.presentation.components

import com.velok.app.domain.model.TipoSerie
import kotlin.test.Test
import kotlin.test.assertEquals

class NumeracionSeriesTest {

    @Test
    fun calentamientoNoCuentaEnLaNumeracion() {
        val tipos = listOf(
            TipoSerie.CALENTAMIENTO,
            TipoSerie.NORMAL,
            TipoSerie.NORMAL,
            TipoSerie.CALENTAMIENTO,
            TipoSerie.NORMAL,
        )
        assertEquals(listOf(0, 1, 2, 0, 3), numerosDeSerie(tipos))
    }
}
