package com.velok.app.domain.model

import kotlinx.datetime.LocalDate

enum class TipoSerie {
    NORMAL,
    /** Serie de calentamiento ("W" en los diseños): no cuenta en la numeración. */
    CALENTAMIENTO,
}

/**
 * Serie planeada. Los valores pueden estar vacíos mientras se edita.
 *
 * @property id identifica la serie dentro de su ejercicio, para editarla sin depender de su posición.
 */
data class Serie(
    val id: Int,
    val tipo: TipoSerie = TipoSerie.NORMAL,
    val pesoKg: Double? = null,
    val repeticiones: Int? = null,
)

data class EjercicioPlan(
    val ejercicio: Ejercicio,
    val series: List<Serie>,
    val descansoSegundos: Int = DESCANSO_PREDETERMINADO_SEGUNDOS,
    val nota: String = "",
) {
    val seriesNormales: List<Serie> get() = series.filter { it.tipo == TipoSerie.NORMAL }

    companion object {
        const val DESCANSO_PREDETERMINADO_SEGUNDOS = 60
    }
}

/** Plan de entrenamiento ("Set del Lunes"): lista ordenada de ejercicios con sus series. */
data class Plan(
    val id: String,
    val nombre: String,
    val categoria: TipoCategoria,
    val imagenUrl: String,
    val fecha: LocalDate,
    val ejercicios: List<EjercicioPlan>,
) {
    val totalSeries: Int get() = ejercicios.sumOf { it.series.size }

    /** Cada serie cuenta su tiempo de trabajo más el descanso de su ejercicio; se redondea hacia arriba. */
    val duracionEstimadaMinutos: Int
        get() {
            val segundos = ejercicios.sumOf { ep -> ep.series.size * (SEGUNDOS_POR_SERIE + ep.descansoSegundos) }
            return (segundos + 59) / 60
        }

    companion object {
        const val SEGUNDOS_POR_SERIE = 45
    }
}
