package com.velok.app.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearsUntil

/** Unidad en la que el usuario prefiere ver y escribir pesos. */
enum class UnidadPeso { KG, LB }

enum class MetaEntrenamiento { PERDER_GRASA, GANAR_MUSCULO, MANTENER }

/**
 * Datos del cuestionario inicial. El peso siempre se guarda en kg, aunque el usuario lo escriba
 * en libras; [unidadPeso] solo indica cómo mostrarlo.
 */
data class PerfilFisico(
    val fechaNacimiento: LocalDate,
    val unidadPeso: UnidadPeso,
    val estaturaCm: Int,
    val pesoKg: Double,
    val meta: MetaEntrenamiento,
)

const val KG_POR_LIBRA = 0.45359237

fun librasAKg(libras: Double): Double = libras * KG_POR_LIBRA

fun kgALibras(kg: Double): Double = kg / KG_POR_LIBRA

/**
 * Regla de unidades: los pesos se guardan siempre en kg. Esta función los pasa a la unidad del
 * usuario solo para mostrarlos (sin redondear; el redondeo es de la presentación).
 */
fun pesoEnUnidad(kg: Double, unidad: UnidadPeso): Double = when (unidad) {
    UnidadPeso.KG -> kg
    UnidadPeso.LB -> kgALibras(kg)
}

/**
 * Lo que el usuario escribe en su unidad, a kg para guardarlo. No se redondea: así 135 lb se
 * guarda como 61.2349... kg y vuelve a mostrarse como 135 lb.
 */
fun pesoAKg(valor: Double, unidad: UnidadPeso): Double = when (unidad) {
    UnidadPeso.KG -> valor
    UnidadPeso.LB -> librasAKg(valor)
}

/** Años cumplidos en [hoy]. */
fun edadEn(fechaNacimiento: LocalDate, hoy: LocalDate): Int = fechaNacimiento.yearsUntil(hoy)
