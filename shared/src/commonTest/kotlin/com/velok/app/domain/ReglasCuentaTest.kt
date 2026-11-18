package com.velok.app.domain

import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.Usuario
import com.velok.app.domain.model.destinoPara
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasCuenta
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReglasCuentaTest {

    @Test
    fun correo_vacioEInvalido() {
        assertEquals(ErrorCampo.VACIO, ReglasCuenta.validarCorreo("   "))
        assertEquals(ErrorCampo.CORREO_INVALIDO, ReglasCuenta.validarCorreo("ejemplo"))
        assertEquals(ErrorCampo.CORREO_INVALIDO, ReglasCuenta.validarCorreo("ejemplo@gmail"))
        assertEquals(ErrorCampo.CORREO_INVALIDO, ReglasCuenta.validarCorreo("con espacio@gmail.com"))
        assertNull(ReglasCuenta.validarCorreo(" ejemplo@gmail.com "))
    }

    @Test
    fun contrasena_minimoNueveCaracteres() {
        assertEquals(ErrorCampo.VACIO, ReglasCuenta.validarContrasena(""))
        assertEquals(ErrorCampo.CONTRASENA_CORTA, ReglasCuenta.validarContrasena("12345678"))
        assertNull(ReglasCuenta.validarContrasena("123456789"))
    }

    @Test
    fun confirmacion_debeCoincidir() {
        assertEquals(ErrorCampo.VACIO, ReglasCuenta.validarConfirmacion("velok2026", ""))
        assertEquals(ErrorCampo.CONTRASENAS_DISTINTAS, ReglasCuenta.validarConfirmacion("velok2026", "velok2027"))
        assertNull(ReglasCuenta.validarConfirmacion("velok2026", "velok2026"))
    }

    @Test
    fun nombre_entreTresYVeinteCaracteres() {
        assertEquals(ErrorCampo.VACIO, ReglasCuenta.validarNombre("  "))
        assertEquals(ErrorCampo.NOMBRE_CORTO, ReglasCuenta.validarNombre("Al"))
        assertEquals(ErrorCampo.NOMBRE_LARGO, ReglasCuenta.validarNombre("a".repeat(21)))
        assertNull(ReglasCuenta.validarNombre("  Checo  "))
    }

    @Test
    fun normalizarCorreo_quitaEspaciosYMayusculas() {
        assertEquals("ana@mail.com", ReglasCuenta.normalizarCorreo("  Ana@Mail.COM "))
    }

    @Test
    fun destinoInicial_segunSesionYPerfil() {
        val sinPerfil = Usuario(id = "1", nombre = "Checo", correo = "checo@velok.app")
        val perfil = PerfilFisico(LocalDate(2004, 4, 25), UnidadPeso.KG, 170, 100.0, MetaEntrenamiento.MANTENER)

        assertEquals(DestinoInicial.BIENVENIDA, destinoPara(null))
        assertEquals(DestinoInicial.FORMULARIO, destinoPara(sinPerfil))
        assertEquals(DestinoInicial.PRINCIPAL, destinoPara(sinPerfil.copy(perfil = perfil)))
    }
}
