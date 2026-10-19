package com.velok.app.domain.calculo

import com.velok.app.domain.model.GrupoMuscular
import com.velok.app.domain.model.HorasSemana
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.PuntoProgreso
import com.velok.app.domain.model.SesionEntrenamiento
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

// Cálculos de estadísticas a partir del historial de sesiones. Son funciones puras: reciben
// todo lo que necesitan (incluida la zona horaria) para poder probarlas con datos fijos.

private fun SesionEntrenamiento.fecha(zona: TimeZone): LocalDate = inicio.toLocalDateTime(zona).date

/** Lunes de la semana de [fecha]. */
fun inicioDeSemana(fecha: LocalDate): LocalDate =
    fecha.minus(fecha.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)

/**
 * Un punto por sesión dentro de [desde]..[hasta]. Si [ejercicioId] no es null solo cuenta ese
 * ejercicio. Solo cuentan series completadas; las sesiones sin datos no generan punto.
 */
fun calcularProgreso(
    historial: List<SesionEntrenamiento>,
    ejercicioId: String?,
    metrica: MetricaProgreso,
    desde: LocalDate,
    hasta: LocalDate,
    zona: TimeZone,
): List<PuntoProgreso> = historial
    .filter { it.fecha(zona) in desde..hasta }
    .sortedBy { it.inicio }
    .mapNotNull { sesion ->
        val series = sesion.ejercicios
            .filter { ejercicioId == null || it.ejercicio.id == ejercicioId }
            .flatMap { it.series }
            .filter { it.completada }
        if (series.isEmpty()) return@mapNotNull null
        val valor = when (metrica) {
            MetricaProgreso.PESO -> series.maxOf { it.pesoKg ?: 0.0 }
            MetricaProgreso.VOLUMEN -> series.sumOf { it.volumenKg }
            MetricaProgreso.REPETICIONES -> series.sumOf { it.repeticiones ?: 0 }.toDouble()
        }
        PuntoProgreso(sesion.fecha(zona), valor)
    }

/**
 * Horas entrenadas por semana (lunes a domingo) desde la semana de [desde] hasta la de [hasta].
 * Incluye las semanas sin entrenamientos con 0 horas. Las sesiones sin terminar no cuentan.
 */
fun calcularHorasPorSemana(
    historial: List<SesionEntrenamiento>,
    desde: LocalDate,
    hasta: LocalDate,
    zona: TimeZone,
): List<HorasSemana> {
    val horasPorSemana = historial
        .filter { it.fin != null }
        .groupBy { inicioDeSemana(it.fecha(zona)) }
        .mapValues { (_, sesiones) ->
            sesiones.sumOf { it.duracion(it.fin!!).inWholeMinutes } / 60.0
        }
    val semanas = mutableListOf<HorasSemana>()
    var semana = inicioDeSemana(desde)
    val ultima = inicioDeSemana(hasta)
    while (semana <= ultima) {
        semanas += HorasSemana(semana, horasPorSemana[semana] ?: 0.0)
        semana = semana.plus(1, DateTimeUnit.WEEK)
    }
    return semanas
}

/**
 * Series completadas por grupo muscular (según el músculo primario de cada ejercicio)
 * dentro de [desde]..[hasta]. Todos los grupos aparecen, aunque sea con 0.
 */
fun calcularDistribucionMuscular(
    historial: List<SesionEntrenamiento>,
    desde: LocalDate,
    hasta: LocalDate,
    zona: TimeZone,
): Map<GrupoMuscular, Int> {
    val conteo = GrupoMuscular.entries.associateWith { 0 }.toMutableMap()
    historial
        .filter { it.fecha(zona) in desde..hasta }
        .flatMap { it.ejercicios }
        .forEach { ej ->
            val grupo = ej.ejercicio.musculoPrimario.grupo
            conteo[grupo] = conteo.getValue(grupo) + ej.series.count { it.completada }
        }
    return conteo
}
