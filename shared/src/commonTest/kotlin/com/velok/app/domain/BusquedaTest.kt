package com.velok.app.domain

import com.velok.app.domain.model.normalizarBusqueda
import kotlin.test.Test
import kotlin.test.assertEquals

class BusquedaTest {

    @Test
    fun quitaMayusculasYAcentos() {
        assertEquals("extension de triceps", normalizarBusqueda("Extensión de TRÍCEPS"))
        assertEquals("jalon al pecho (maquina)", normalizarBusqueda("Jalón al pecho (Máquina)"))
        assertEquals("pinguino", normalizarBusqueda("Pingüino"))
        assertEquals("nandu", normalizarBusqueda("ÑANDÚ"))
    }

    @Test
    fun quitaEspaciosDeMas() {
        assertEquals("bench press", normalizarBusqueda("  Bench   PRESS "))
    }

    @Test
    fun loEscritoYElNombreGuardadoCoinciden() {
        val guardado = normalizarBusqueda("Curl de bíceps (Mancuernas)")
        for (escrito in listOf("biceps", "BÍCEPS", "Bíceps", "curl de bi")) {
            assertEquals(true, normalizarBusqueda(escrito) in guardado, escrito)
        }
    }

    @Test
    fun dejaIgualLoQueNoEsLetraConAcento() {
        assertEquals("45° side bend", normalizarBusqueda("45° side bend"))
        assertEquals("100% y_algo", normalizarBusqueda("100% Y_ALGO"))
    }
}
