package com.velok.app.domain

import com.velok.app.domain.model.edadEn
import com.velok.app.domain.model.kgALibras
import com.velok.app.domain.model.librasAKg
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasPerfil
import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReglasPerfilTest {

    private val hoy = LocalDate(2026, 11, 4)

    @Test
    fun edad_cuentaSoloCumpleanosPasados() {
        assertEquals(22, edadEn(LocalDate(2004, 4, 25), hoy))
        assertEquals(17, edadEn(LocalDate(2008, 11, 5), hoy))  // cumple 18 mañana
        assertEquals(18, edadEn(LocalDate(2008, 11, 4), hoy))  // cumple 18 hoy
    }

    @Test
    fun fechaNacimiento_mayorDeEdadYNoFutura() {
        assertNull(ReglasPerfil.validarFechaNacimiento(LocalDate(2008, 11, 4), hoy))
        assertEquals(ErrorCampo.MENOR_DE_EDAD, ReglasPerfil.validarFechaNacimiento(LocalDate(2008, 11, 5), hoy))
        assertEquals(ErrorCampo.FECHA_FUTURA, ReglasPerfil.validarFechaNacimiento(LocalDate(2027, 1, 1), hoy))
    }

    @Test
    fun estatura_entre100y250() {
        assertEquals(ErrorCampo.VACIO, ReglasPerfil.validarEstatura(null))
        assertEquals(ErrorCampo.ESTATURA_FUERA_DE_RANGO, ReglasPerfil.validarEstatura(99))
        assertEquals(ErrorCampo.ESTATURA_FUERA_DE_RANGO, ReglasPerfil.validarEstatura(251))
        assertNull(ReglasPerfil.validarEstatura(170))
    }

    @Test
    fun peso_entre30y300Kg() {
        assertEquals(ErrorCampo.VACIO, ReglasPerfil.validarPeso(null))
        assertEquals(ErrorCampo.PESO_FUERA_DE_RANGO, ReglasPerfil.validarPeso(29.9))
        assertNull(ReglasPerfil.validarPeso(100.0))
    }

    @Test
    fun conversionLibras_idaYVuelta() {
        assertTrue(abs(librasAKg(220.0) - 99.79) < 0.01)
        assertTrue(abs(kgALibras(librasAKg(155.0)) - 155.0) < 1e-9)
        // 66 lb (≈ 29.9 kg) queda fuera del rango; 67 lb (≈ 30.4 kg) dentro.
        assertEquals(ErrorCampo.PESO_FUERA_DE_RANGO, ReglasPerfil.validarPeso(librasAKg(66.0)))
        assertNull(ReglasPerfil.validarPeso(librasAKg(67.0)))
    }
}
