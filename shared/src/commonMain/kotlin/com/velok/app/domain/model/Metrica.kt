package com.velok.app.domain.model

enum class TipoMetrica {
    CALORIAS,
    PASOS,
    DISTANCIA,
    RITMO_CARDIACO,
}

/**
 * Valor de una métrica de salud con su historial del día.
 *
 * @property valor en la unidad base del tipo: kcal, pasos, km o lpm. null = sin datos (la pantalla
 *   muestra "--", nunca un 0 que parecería una medición real).
 * @property historial valores por hora para la mini gráfica, del más antiguo al más nuevo.
 */
data class Metrica(
    val tipo: TipoMetrica,
    val valor: Double?,
    val historial: List<Float>,
)
