package com.velok.app.data.local.seguridad

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.CoreCrypto.kCCSuccess
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecRandomDefault

// Implementación iOS de expect/actual: usa CommonCrypto y Security de Apple desde Kotlin/Native.

@OptIn(ExperimentalForeignApi::class)
actual fun derivarPbkdf2Sha256(contrasena: String, sal: ByteArray, iteraciones: Int, bytes: Int): ByteArray {
    val clave = contrasena.encodeToByteArray()
    require(clave.isNotEmpty() && sal.isNotEmpty()) { "La contraseña y la sal no pueden estar vacías" }
    val salida = ByteArray(bytes)
    val resultado = clave.usePinned { c ->
        sal.usePinned { s ->
            salida.usePinned { o ->
                CCKeyDerivationPBKDF(
                    kCCPBKDF2,
                    c.addressOf(0),
                    clave.size.convert(),
                    s.addressOf(0).reinterpret<UByteVar>(),
                    sal.size.convert(),
                    kCCPRFHmacAlgSHA256,
                    iteraciones.convert(),
                    o.addressOf(0).reinterpret<UByteVar>(),
                    salida.size.convert(),
                )
            }
        }
    }
    check(resultado == kCCSuccess) { "CCKeyDerivationPBKDF falló: $resultado" }
    return salida
}

@OptIn(ExperimentalForeignApi::class)
actual fun bytesAleatoriosSeguros(cantidad: Int): ByteArray {
    val salida = ByteArray(cantidad)
    val resultado = salida.usePinned { SecRandomCopyBytes(kSecRandomDefault, cantidad.convert(), it.addressOf(0)) }
    check(resultado == errSecSuccess) { "SecRandomCopyBytes falló: $resultado" }
    return salida
}
