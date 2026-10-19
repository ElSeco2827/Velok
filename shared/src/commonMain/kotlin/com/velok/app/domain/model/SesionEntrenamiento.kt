package com.velok.app.domain.model

import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Serie durante una sesión.
 *
 * @property anterior lo que dice el plan para esta serie (columna "ANTERIOR" de los diseños).
 */
data class SerieSesion(
    val id: Int,
    val tipo: TipoSerie,
    val anterior: Serie?,
    val pesoKg: Double?,
    val repeticiones: Int?,
    val completada: Boolean = false,
) {
    /** Volumen = peso × repeticiones. Una serie sin peso (peso corporal) aporta 0 kg. */
    val volumenKg: Double get() = (pesoKg ?: 0.0) * (repeticiones ?: 0)
}

data class EjercicioSesion(
    val ejercicio: Ejercicio,
    val series: List<SerieSesion>,
    val descansoSegundos: Int,
    val nota: String = "",
)

/** Entrenamiento en curso (fin = null) o terminado. */
data class SesionEntrenamiento(
    val id: String,
    val planId: String,
    val nombrePlan: String,
    val inicio: Instant,
    val ejercicios: List<EjercicioSesion>,
    val fin: Instant? = null,
) {
    private val completadas: List<SerieSesion>
        get() = ejercicios.flatMap { ej -> ej.series.filter { it.completada } }

    /** Solo cuentan las series completadas. */
    val volumenKg: Double get() = completadas.sumOf { it.volumenKg }

    val seriesCompletadas: Int get() = completadas.size

    fun duracion(ahora: Instant): Duration = (fin ?: ahora) - inicio
}

// Reglas de la sesión: funciones puras que devuelven una copia nueva.

/** Crea la sesión a partir del plan: cada serie empieza con los valores planeados. */
fun Plan.iniciarSesion(id: String, inicio: Instant): SesionEntrenamiento = SesionEntrenamiento(
    id = id,
    planId = this.id,
    nombrePlan = nombre,
    inicio = inicio,
    ejercicios = ejercicios.map { ep ->
        EjercicioSesion(
            ejercicio = ep.ejercicio,
            descansoSegundos = ep.descansoSegundos,
            nota = ep.nota,
            series = ep.series.map { serie ->
                SerieSesion(
                    id = serie.id,
                    tipo = serie.tipo,
                    anterior = serie,
                    pesoKg = serie.pesoKg,
                    repeticiones = serie.repeticiones,
                )
            },
        )
    },
)

/**
 * Marca o desmarca una serie. No se puede completar una serie sin repeticiones:
 * en ese caso la sesión no cambia.
 */
fun SesionEntrenamiento.alternarCompletada(indiceEjercicio: Int, idSerie: Int): SesionEntrenamiento =
    actualizarSerie(indiceEjercicio, idSerie) { serie ->
        when {
            serie.completada -> serie.copy(completada = false)
            (serie.repeticiones ?: 0) <= 0 -> serie
            else -> serie.copy(completada = true)
        }
    }

fun SesionEntrenamiento.cambiarValores(
    indiceEjercicio: Int,
    idSerie: Int,
    pesoKg: Double?,
    repeticiones: Int?,
): SesionEntrenamiento = actualizarSerie(indiceEjercicio, idSerie) {
    it.copy(pesoKg = pesoKg, repeticiones = repeticiones)
}

/** Agrega una serie normal con los valores de la última, sin completar. */
fun SesionEntrenamiento.agregarSerie(indiceEjercicio: Int): SesionEntrenamiento =
    actualizarEjercicio(indiceEjercicio) { ej ->
        val ultima = ej.series.lastOrNull()
        val nueva = SerieSesion(
            id = (ej.series.maxOfOrNull { it.id } ?: 0) + 1,
            tipo = TipoSerie.NORMAL,
            anterior = null,
            pesoKg = ultima?.pesoKg,
            repeticiones = ultima?.repeticiones,
        )
        ej.copy(series = ej.series + nueva)
    }

fun SesionEntrenamiento.eliminarEjercicio(indice: Int): SesionEntrenamiento =
    copy(ejercicios = ejercicios.filterIndexed { i, _ -> i != indice })

fun SesionEntrenamiento.cambiarNota(indiceEjercicio: Int, nota: String): SesionEntrenamiento =
    actualizarEjercicio(indiceEjercicio) { it.copy(nota = nota) }

private inline fun SesionEntrenamiento.actualizarEjercicio(
    indice: Int,
    cambio: (EjercicioSesion) -> EjercicioSesion,
): SesionEntrenamiento {
    if (indice !in ejercicios.indices) return this
    return copy(ejercicios = ejercicios.mapIndexed { i, ej -> if (i == indice) cambio(ej) else ej })
}

private inline fun SesionEntrenamiento.actualizarSerie(
    indiceEjercicio: Int,
    idSerie: Int,
    cambio: (SerieSesion) -> SerieSesion,
): SesionEntrenamiento = actualizarEjercicio(indiceEjercicio) { ej ->
    ej.copy(series = ej.series.map { if (it.id == idSerie) cambio(it) else it })
}
