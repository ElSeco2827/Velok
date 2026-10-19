package com.velok.app.domain.model

/** Equipo necesario para un ejercicio (filtros de la pantalla Ejercicio). */
enum class TipoEquipo {
    BARRA,
    PESA,
    TAPETE,
    MAQUINA,
}

/** Grupos que se comparan en la gráfica de radar del perfil. */
enum class GrupoMuscular {
    ESPALDA,
    PECHO,
    CORE,
    HOMBROS,
    BRAZOS,
    PIERNAS,
}

/** Músculo trabajado. Cada músculo pertenece a un grupo (regla de negocio para las estadísticas). */
enum class Musculo(val grupo: GrupoMuscular) {
    PECHO(GrupoMuscular.PECHO),
    DORSALES(GrupoMuscular.ESPALDA),
    LUMBARES(GrupoMuscular.ESPALDA),
    HOMBROS(GrupoMuscular.HOMBROS),
    BICEPS(GrupoMuscular.BRAZOS),
    TRICEPS(GrupoMuscular.BRAZOS),
    ANTEBRAZOS(GrupoMuscular.BRAZOS),
    ABDOMINALES(GrupoMuscular.CORE),
    CUADRICEPS(GrupoMuscular.PIERNAS),
    ISQUIOTIBIALES(GrupoMuscular.PIERNAS),
    GLUTEOS(GrupoMuscular.PIERNAS),
    PANTORRILLAS(GrupoMuscular.PIERNAS),
}

/**
 * Ejercicio del catálogo.
 *
 * @property nombre viene de la fuente de datos (hoy mock; después ExerciseDB), no de los recursos de UI.
 * @property seriesSugeridas y [repeticionesSugeridas] se usan al agregarlo a un plan ("12 × 4 series").
 */
data class Ejercicio(
    val id: String,
    val nombre: String,
    val categoria: TipoCategoria,
    val equipo: TipoEquipo,
    val musculoPrimario: Musculo,
    val musculosSecundarios: List<Musculo>,
    val imagenUrl: String,
    val seriesSugeridas: Int = 4,
    val repeticionesSugeridas: Int = 12,
)
