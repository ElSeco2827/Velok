package com.velok.app.presentation.util

import kotlin.math.abs
import kotlin.math.roundToLong

/** Formatea un entero con separador de miles: 2032 -> "2,032". */
fun formatearMiles(valor: Long): String {
    val digitos = abs(valor).toString()
    val agrupado = digitos.reversed().chunked(3).joinToString(",").reversed()
    return if (valor < 0) "-$agrupado" else agrupado
}

/** Formatea con un número fijo de decimales: (1.5, 1) -> "1.5", (2.0, 1) -> "2.0". */
fun formatearDecimal(valor: Double, decimales: Int = 1): String {
    if (decimales <= 0) return formatearMiles(valor.roundToLong())
    var factor = 1L
    repeat(decimales) { factor *= 10 }
    val escalado = abs(valor * factor).roundToLong()
    val entero = formatearMiles(escalado / factor)
    val fraccion = (escalado % factor).toString().padStart(decimales, '0')
    val signo = if (valor < 0 && escalado != 0L) "-" else ""
    return "$signo$entero.$fraccion"
}
