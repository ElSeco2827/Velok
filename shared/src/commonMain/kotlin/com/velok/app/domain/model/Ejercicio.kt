package com.velok.app.domain.model

/** Equipo necesario para un ejercicio (filtros de la pantalla Ejercicio). */
enum class TipoEquipo {
    BARRA,
    PESA,
    TAPETE,
    MAQUINA,

    /**
     * Accesorios y lo que no encaja en los otros (bandas, cuerdas, balones, rodillos...). No tiene
     * filtro propio: estos ejercicios solo aparecen cuando no hay filtro de equipo ([filtrables]).
     */
    OTRO,
    ;

    companion object {
        /** Equipos que se pueden elegir como filtro (los 4 chips del diseño). */
        val filtrables = listOf(BARRA, PESA, TAPETE, MAQUINA)
    }
}

/** De dónde viene un ejercicio del catálogo. */
enum class OrigenEjercicio {
    /** Los 21 de ejemplo (en español). Siguen disponibles para los planes e historial que los usan. */
    EJEMPLO,

    /** Descargado de la API de ExerciseDB (nombre en inglés, imagen GIF). */
    EXERCISEDB,
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
 * @property nombre viene de la fuente de datos, no de los recursos de UI: en español en los de
 * ejemplo y en inglés en los de ExerciseDB (no se traduce).
 * @property imagenUrl en los de ExerciseDB es un GIF remoto (animado en Android, fijo en iOS).
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
    val origen: OrigenEjercicio = OrigenEjercicio.EJEMPLO,
)
