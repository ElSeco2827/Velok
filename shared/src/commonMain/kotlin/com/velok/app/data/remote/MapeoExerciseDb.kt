package com.velok.app.data.remote

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.OrigenEjercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo

/**
 * Conversión de los datos de ExerciseDB (en inglés) a nuestro modelo. La API tiene 10 partes del
 * cuerpo, 30 equipos y 38 músculos; nuestro dominio agrupa en menos valores, así que cada tabla
 * junta varios valores de la API en uno nuestro.
 *
 * Idioma: el nombre del ejercicio se deja en inglés (como lo manda la API); categoría, equipo y
 * músculos son enums del dominio y la pantalla los muestra en español desde strings.xml.
 *
 * Valores que no encajan:
 * - Parte del cuerpo desconocida: categoría FUERZA.
 * - Equipo desconocido: OTRO.
 * - Músculo sin equivalente (cardiovascular system, hands, feet...): se descarta de los
 *   secundarios; si es el principal, se usa el músculo de su parte del cuerpo.
 * Así un valor nuevo de la API nunca impide guardar el ejercicio.
 */
object MapeoExerciseDb {

    /** Valores de `GET /api/v1/bodyparts`. */
    private val categorias = mapOf(
        "chest" to TipoCategoria.PECHO,
        "back" to TipoCategoria.ESPALDA,
        "shoulders" to TipoCategoria.HOMBROS,
        "upper arms" to TipoCategoria.BRAZOS,
        "lower arms" to TipoCategoria.BRAZOS,
        "upper legs" to TipoCategoria.PIERNA,
        "lower legs" to TipoCategoria.PIERNA,
        "waist" to TipoCategoria.ABDOMEN,
        "cardio" to TipoCategoria.CARDIO,
        // El cuello no tiene categoría propia: va a la general de fuerza.
        "neck" to TipoCategoria.FUERZA,
    )

    /** Músculo principal cuando el de la API no tiene equivalente, según la parte del cuerpo. */
    private val musculoPorParte = mapOf(
        "chest" to Musculo.PECHO,
        "back" to Musculo.DORSALES,
        "shoulders" to Musculo.HOMBROS,
        "upper arms" to Musculo.BICEPS,
        "lower arms" to Musculo.ANTEBRAZOS,
        "upper legs" to Musculo.CUADRICEPS,
        "lower legs" to Musculo.PANTORRILLAS,
        "waist" to Musculo.ABDOMINALES,
        // En cardio trabajan sobre todo las piernas.
        "cardio" to Musculo.CUADRICEPS,
        "neck" to Musculo.HOMBROS,
    )

    /** Valores de `GET /api/v1/equipments`. Peso corporal y asistido van a TAPETE. */
    private val equipos = mapOf(
        "barbell" to TipoEquipo.BARRA,
        "olympic barbell" to TipoEquipo.BARRA,
        "ez bar" to TipoEquipo.BARRA,
        "trap bar" to TipoEquipo.BARRA,
        "dumbbell" to TipoEquipo.PESA,
        "kettlebell" to TipoEquipo.PESA,
        "weighted" to TipoEquipo.PESA,
        "cable" to TipoEquipo.MAQUINA,
        "leverage machine" to TipoEquipo.MAQUINA,
        "smith machine" to TipoEquipo.MAQUINA,
        "sled machine" to TipoEquipo.MAQUINA,
        "stepmill machine" to TipoEquipo.MAQUINA,
        "elliptical machine" to TipoEquipo.MAQUINA,
        "stationary bike" to TipoEquipo.MAQUINA,
        "ski ergometer" to TipoEquipo.MAQUINA,
        "upper body ergometer" to TipoEquipo.MAQUINA,
        "bodyweight" to TipoEquipo.TAPETE,
        "assisted" to TipoEquipo.TAPETE,
        // Accesorios: resistance band, rope, stability ball, medicine ball, bosu ball, roller,
        // tire, ab wheel, towel, tennis ball, suspension trainer, hammer -> OTRO (valor por defecto).
    )

    /** Valores de `GET /api/v1/muscles`. Los que no aparecen no tienen equivalente. */
    private val musculos = mapOf(
        "pectorals" to Musculo.PECHO,
        "serratus anterior" to Musculo.PECHO,
        "latissimus dorsi" to Musculo.DORSALES,
        "rhomboids" to Musculo.DORSALES,
        "upper back" to Musculo.DORSALES,
        "back" to Musculo.DORSALES,
        "erector spinae" to Musculo.LUMBARES,
        "deltoids" to Musculo.HOMBROS,
        "rear deltoids" to Musculo.HOMBROS,
        "rotator cuff" to Musculo.HOMBROS,
        "trapezius" to Musculo.HOMBROS,
        "levator scapulae" to Musculo.HOMBROS,
        "biceps" to Musculo.BICEPS,
        "brachialis" to Musculo.BICEPS,
        "triceps" to Musculo.TRICEPS,
        "forearms" to Musculo.ANTEBRAZOS,
        "wrists" to Musculo.ANTEBRAZOS,
        "wrist flexors" to Musculo.ANTEBRAZOS,
        "wrist extensors" to Musculo.ANTEBRAZOS,
        "grip muscles" to Musculo.ANTEBRAZOS,
        "abdominals" to Musculo.ABDOMINALES,
        "obliques" to Musculo.ABDOMINALES,
        "core" to Musculo.ABDOMINALES,
        "hip flexors" to Musculo.ABDOMINALES,
        "quadriceps" to Musculo.CUADRICEPS,
        "hamstrings" to Musculo.ISQUIOTIBIALES,
        "glutes" to Musculo.GLUTEOS,
        "adductors" to Musculo.GLUTEOS,
        "abductors" to Musculo.GLUTEOS,
        "calves" to Musculo.PANTORRILLAS,
        "soleus" to Musculo.PANTORRILLAS,
        "tibialis anterior" to Musculo.PANTORRILLAS,
    )

    private fun clave(texto: String) = texto.trim().lowercase()

    fun categoria(parteDelCuerpo: String?): TipoCategoria =
        parteDelCuerpo?.let { categorias[clave(it)] } ?: TipoCategoria.FUERZA

    /** Se usa el primer equipo de la lista (algunos ejercicios traen dos). */
    fun equipo(equiposApi: List<String>): TipoEquipo =
        equiposApi.firstOrNull()?.let { equipos[clave(it)] } ?: TipoEquipo.OTRO

    fun musculo(nombre: String): Musculo? = musculos[clave(nombre)]

    /** null si el ejercicio no tiene id o nombre (no se puede guardar ni mostrar). */
    fun aDominio(dto: EjercicioDto): Ejercicio? {
        val id = dto.exerciseId.trim()
        val nombre = dto.name.trim()
        if (id.isEmpty() || nombre.isEmpty()) return null

        val parte = dto.bodyParts.firstOrNull()
        val categoria = categoria(parte)
        val primario = dto.targetMuscles.firstNotNullOfOrNull(::musculo)
            ?: parte?.let { musculoPorParte[clave(it)] }
            ?: Musculo.ABDOMINALES
        val secundarios = dto.secondaryMuscles.mapNotNull(::musculo).distinct() - primario
        return Ejercicio(
            id = id,
            // Solo se pone en mayúscula la primera letra ("upward facing dog" -> "Upward facing dog").
            nombre = nombre.replaceFirstChar { it.uppercaseChar() },
            categoria = categoria,
            equipo = equipo(dto.equipments),
            musculoPrimario = primario,
            musculosSecundarios = secundarios,
            imagenUrl = dto.gifUrl?.trim().orEmpty(),
            origen = OrigenEjercicio.EXERCISEDB,
        )
    }
}
