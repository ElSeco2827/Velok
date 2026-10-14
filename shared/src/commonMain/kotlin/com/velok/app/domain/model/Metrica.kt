package com.velok.app.domain.model

enum class TipoMetrica {
    CALORIAS,
    PASOS,
    DISTANCIA,
    RITMO_CARDIACO,
}

/**
 * Valor de una métrica de salud con su historial reciente.
 *
 * @property valor en la unidad base del tipo: kcal, pasos, km o lpm.
 * @property historial muestras recientes para la mini gráfica, de la más antigua a la más nueva.
 */
data class Metrica(
    val tipo: TipoMetrica,
    val valor: Double,
    val historial: List<Float>,
)
