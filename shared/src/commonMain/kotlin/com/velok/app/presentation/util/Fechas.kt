package com.velok.app.presentation.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** Días del mes [mes] (1-12) de [anio], considerando años bisiestos. */
fun diasEnMes(anio: Int, mes: Int): Int =
    LocalDate(anio, mes, 1).plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day

/** Crea la fecha ajustando el día si el mes es más corto (31 de febrero -> 28 o 29). */
fun fechaAjustada(anio: Int, mes: Int, dia: Int): LocalDate =
    LocalDate(anio, mes, dia.coerceIn(1, diasEnMes(anio, mes)))
