package com.velok.app.domain.model

import kotlinx.datetime.LocalDate

/** Qué se grafica en la tarjeta "Resumen". */
enum class MetricaProgreso {
    /** Peso máximo levantado en la sesión. */
    PESO,
    /** Suma de peso × repeticiones. */
    VOLUMEN,
    /** Suma de repeticiones. */
    REPETICIONES,
}

/** Periodo hacia atrás desde hoy (incluido). */
enum class RangoTiempo(val dias: Int) {
    TRES_DIAS(3),
    SEMANA(7),
    MES(30),
    TRES_MESES(90),
}

/** Un punto de la gráfica de progreso: una sesión. */
data class PuntoProgreso(val fecha: LocalDate, val valor: Double)

/** Horas entrenadas en la semana que empieza (lunes) en [inicioSemana]. */
data class HorasSemana(val inicioSemana: LocalDate, val horas: Double)

data class ResumenHoras(
    val horasEstaSemana: Double,
    val semanas: List<HorasSemana>,
)

/** Series completadas por grupo muscular en el periodo y en el periodo anterior de igual duración. */
data class ComparacionMuscular(
    val actual: Map<GrupoMuscular, Int>,
    val anterior: Map<GrupoMuscular, Int>,
)
