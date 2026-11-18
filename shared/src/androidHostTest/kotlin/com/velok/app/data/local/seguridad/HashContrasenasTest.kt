package com.velok.app.data.local.seguridad

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Prueba la implementación Android (javax.crypto), que también corre en la JVM de la computadora. */
class HashContrasenasTest {

    private val hash = HashContrasenas(iteraciones = 1_000)

    @Test
    fun coincideConElVectorDePruebaDelRfc7914() {
        // RFC 7914, sección 11: PBKDF2-HMAC-SHA256, P = "passwd", S = "salt", c = 1, dkLen = 64.
        val esperado = (
            "55ac046e56e3089fec1691c22544b605f94185216dde0465e68b9d57c20dacbc" +
                "49ca9cccf179b645991664b39d77ef317c71b845b1e30bd509112041d3a19783"
            ).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        assertContentEquals(esperado, derivarPbkdf2Sha256("passwd", "salt".encodeToByteArray(), 1, 64))
    }

    @Test
    fun verificaLaContrasenaCorrectaYRechazaOtras() {
        val guardado = hash.generar("velok2026")
        assertTrue(hash.verificar("velok2026", guardado))
        assertFalse(hash.verificar("velok2025", guardado))
        assertFalse(hash.verificar("", guardado))
    }

    @Test
    fun usaElFormatoAcordadoYUnaSalDistintaCadaVez() {
        val a = hash.generar("velok2026")
        val b = hash.generar("velok2026")
        assertNotEquals(a, b)
        val partes = a.split("$")
        assertEquals(4, partes.size)
        assertEquals("pbkdf2-sha256", partes[0])
        assertEquals("1000", partes[1])
    }

    @Test
    fun lasIteracionesGuardadasMandanAlVerificar() {
        // Un hash generado con otras iteraciones se sigue verificando (permite subirlas en el futuro).
        val guardado = HashContrasenas(iteraciones = 2_000).generar("velok2026")
        assertTrue(hash.verificar("velok2026", guardado))
    }

    @Test
    fun rechazaTextoQueNoEsUnHash() {
        assertFalse(hash.verificar("velok2026", "velok2026"))
        assertFalse(hash.verificar("velok2026", "pbkdf2-sha256\$abc\$\$"))
        assertFalse(hash.verificar("velok2026", "pbkdf2-sha256\$1000\$no-base64!\$xx"))
    }
}
