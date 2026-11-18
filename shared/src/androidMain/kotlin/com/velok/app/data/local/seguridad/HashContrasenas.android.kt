package com.velok.app.data.local.seguridad

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Implementación Android de expect/actual: usa la criptografía de la plataforma (javax.crypto).

actual fun derivarPbkdf2Sha256(contrasena: String, sal: ByteArray, iteraciones: Int, bytes: Int): ByteArray {
    val especificacion = PBEKeySpec(contrasena.toCharArray(), sal, iteraciones, bytes * 8)
    try {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).encoded
    } finally {
        especificacion.clearPassword()
    }
}

actual fun bytesAleatoriosSeguros(cantidad: Int): ByteArray = ByteArray(cantidad).also { SecureRandom().nextBytes(it) }
