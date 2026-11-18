package com.velok.app.domain.validacion

import com.velok.app.domain.model.edadEn
import kotlinx.datetime.LocalDate

/** Por qué un campo no es válido. La presentación lo traduce a un mensaje. */
enum class ErrorCampo {
    VACIO,
    CORREO_INVALIDO,
    CONTRASENA_CORTA,
    CONTRASENAS_DISTINTAS,
    NOMBRE_CORTO,
    NOMBRE_LARGO,
    FECHA_FUTURA,
    MENOR_DE_EDAD,
    ESTATURA_FUERA_DE_RANGO,
    PESO_FUERA_DE_RANGO,
}

/** Reglas de las cuentas. Funciones puras: devuelven el error o null si el valor es válido. */
object ReglasCuenta {
    /** Los diseños indican "Mínimo 9 caracteres". */
    const val CONTRASENA_MINIMA = 9
    const val NOMBRE_MINIMO = 3
    const val NOMBRE_MAXIMO = 20

    private val formatoCorreo = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")

    fun validarNombre(nombre: String): ErrorCampo? {
        val limpio = nombre.trim()
        return when {
            limpio.isEmpty() -> ErrorCampo.VACIO
            limpio.length < NOMBRE_MINIMO -> ErrorCampo.NOMBRE_CORTO
            limpio.length > NOMBRE_MAXIMO -> ErrorCampo.NOMBRE_LARGO
            else -> null
        }
    }

    fun validarCorreo(correo: String): ErrorCampo? {
        val limpio = correo.trim()
        return when {
            limpio.isEmpty() -> ErrorCampo.VACIO
            !formatoCorreo.matches(limpio) -> ErrorCampo.CORREO_INVALIDO
            else -> null
        }
    }

    fun validarContrasena(contrasena: String): ErrorCampo? = when {
        contrasena.isEmpty() -> ErrorCampo.VACIO
        contrasena.length < CONTRASENA_MINIMA -> ErrorCampo.CONTRASENA_CORTA
        else -> null
    }

    fun validarConfirmacion(contrasena: String, confirmacion: String): ErrorCampo? = when {
        confirmacion.isEmpty() -> ErrorCampo.VACIO
        confirmacion != contrasena -> ErrorCampo.CONTRASENAS_DISTINTAS
        else -> null
    }

    /** Correo sin espacios y en minúsculas, para que "Ana@Mail.com " y "ana@mail.com" sean la misma cuenta. */
    fun normalizarCorreo(correo: String): String = correo.trim().lowercase()
}

/** Reglas del cuestionario inicial. */
object ReglasPerfil {
    /** Edad mínima de los términos y condiciones ("Debes tener al menos [18] años"). */
    const val EDAD_MINIMA = 18
    val ESTATURA_CM = 100..250
    val PESO_KG = 30.0..300.0

    fun validarFechaNacimiento(fecha: LocalDate, hoy: LocalDate): ErrorCampo? = when {
        fecha > hoy -> ErrorCampo.FECHA_FUTURA
        edadEn(fecha, hoy) < EDAD_MINIMA -> ErrorCampo.MENOR_DE_EDAD
        else -> null
    }

    fun validarEstatura(cm: Int?): ErrorCampo? = when {
        cm == null -> ErrorCampo.VACIO
        cm !in ESTATURA_CM -> ErrorCampo.ESTATURA_FUERA_DE_RANGO
        else -> null
    }

    /** Se valida en kg, sin importar la unidad en la que se escribió. */
    fun validarPeso(kg: Double?): ErrorCampo? = when {
        kg == null -> ErrorCampo.VACIO
        kg !in PESO_KG -> ErrorCampo.PESO_FUERA_DE_RANGO
        else -> null
    }
}
