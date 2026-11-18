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

/** Años cumplidos en [hoy]. */
fun edadEn(fechaNacimiento: LocalDate, hoy: LocalDate): Int = fechaNacimiento.yearsUntil(hoy)
