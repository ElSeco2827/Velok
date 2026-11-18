package com.velok.app.data.local.seguridad

import kotlin.io.encoding.Base64

/**
 * PBKDF2-HMAC-SHA256 de la contraseña. Kotlin común no trae funciones criptográficas, así que cada
 * plataforma usa la suya (ejemplo de código específico de plataforma con expect/actual):
 * Android, javax.crypto; iOS, CommonCrypto.
 */
expect fun derivarPbkdf2Sha256(contrasena: String, sal: ByteArray, iteraciones: Int, bytes: Int): ByteArray

/** Bytes aleatorios criptográficamente seguros (Android: SecureRandom; iOS: SecRandomCopyBytes). */
expect fun bytesAleatoriosSeguros(cantidad: Int): ByteArray

/**
 * Hash de contraseñas para guardarlas en la base de datos sin texto plano.
 *
 * Formato guardado: `pbkdf2-sha256$iteraciones$sal$hash` (sal y hash en Base64). Guardar las
 * iteraciones permite subirlas en el futuro sin invalidar las contraseñas existentes.
 *
 * @param iteraciones las pruebas usan menos para ser rápidas.
 */
class HashContrasenas(private val iteraciones: Int = ITERACIONES) {

    fun generar(contrasena: String): String {
        val sal = bytesAleatoriosSeguros(BYTES_SAL)
        val hash = derivarPbkdf2Sha256(contrasena, sal, iteraciones, BYTES_HASH)
        return listOf(ALGORITMO, iteraciones.toString(), Base64.encode(sal), Base64.encode(hash)).joinToString("$")
    }

    /** Recalcula el hash con la sal guardada y lo compara en tiempo constante. */
    fun verificar(contrasena: String, guardado: String): Boolean {
        val partes = guardado.split("$")
        if (partes.size != 4 || partes[0] != ALGORITMO) return false
        val vueltas = partes[1].toIntOrNull()?.takeIf { it > 0 } ?: return false
        val (sal, esperado) = runCatching { Base64.decode(partes[2]) to Base64.decode(partes[3]) }.getOrNull() ?: return false
        if (contrasena.isEmpty()) return false
        return igualesEnTiempoConstante(derivarPbkdf2Sha256(contrasena, sal, vueltas, esperado.size), esperado)
    }

    /** Recorre siempre todos los bytes: el tiempo no revela en qué posición difieren. */
    private fun igualesEnTiempoConstante(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diferencia = 0
        for (i in a.indices) diferencia = diferencia or (a[i].toInt() xor b[i].toInt())
        return diferencia == 0
    }

    companion object {
        const val ALGORITMO = "pbkdf2-sha256"

        /**
         * 120,000 iteraciones: en un teléfono de gama media el cálculo tarda del orden de 0.1 a
         * 0.3 s, aceptable al iniciar sesión. OWASP (2023) recomienda 600,000 para servidores;
         * aquí se prioriza que el inicio de sesión no se sienta lento en el teléfono. Si se sube,
         * las contraseñas ya guardadas siguen funcionando porque cada hash guarda sus iteraciones.
         */
        const val ITERACIONES = 120_000
        const val BYTES_SAL = 16
        const val BYTES_HASH = 32
    }
}
