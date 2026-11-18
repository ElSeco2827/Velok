package com.velok.app.data.repository

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.Usuario
import com.velok.app.domain.model.iniciarSesion
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import velok.shared.generated.resources.Res

/**
 * Datos de ejemplo. Los usan los repositorios mock y, la primera vez que se abre la app, se
 * copian a la base de datos local (SembradorDatos). Las imágenes son placeholders locales
 * expuestos como URI, así la UI las carga con Coil igual que cargará las URLs de ExerciseDB.
 */
internal object DatosMock {

    fun imagen(categoria: TipoCategoria): String =
        Res.getUri("drawable/categoria_${categoria.name.lowercase()}.jpg")

    private fun ejercicio(
        id: String,
        nombre: String,
        categoria: TipoCategoria,
        equipo: TipoEquipo,
        primario: Musculo,
        vararg secundarios: Musculo,
    ) = Ejercicio(id, nombre, categoria, equipo, primario, secundarios.toList(), imagen(categoria))

    val ejercicios: List<Ejercicio> = listOf(
        ejercicio("press-banca-barra", "Press de banca plano (Barra)", TipoCategoria.PECHO, TipoEquipo.BARRA, Musculo.PECHO, Musculo.TRICEPS, Musculo.HOMBROS),
        ejercicio("press-inclinado-barra", "Press inclinado (Barra)", TipoCategoria.PECHO, TipoEquipo.BARRA, Musculo.PECHO, Musculo.HOMBROS, Musculo.TRICEPS),
        ejercicio("aperturas-polea", "Aperturas en polea", TipoCategoria.PECHO, TipoEquipo.MAQUINA, Musculo.PECHO, Musculo.HOMBROS),
        ejercicio("fondos-pecho", "Fondos para pecho", TipoCategoria.PECHO, TipoEquipo.MAQUINA, Musculo.PECHO, Musculo.TRICEPS),
        ejercicio("press-banca-mancuernas", "Press de banca (Mancuernas)", TipoCategoria.PECHO, TipoEquipo.PESA, Musculo.PECHO, Musculo.TRICEPS),
        ejercicio("aperturas-mancuernas", "Aperturas (Mancuernas)", TipoCategoria.PECHO, TipoEquipo.PESA, Musculo.PECHO),
        ejercicio("sentadilla-barra", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA, Musculo.CUADRICEPS, Musculo.ISQUIOTIBIALES, Musculo.GLUTEOS),
        ejercicio("curl-piernas-acostado", "Curl de piernas acostado (Máquina)", TipoCategoria.PIERNA, TipoEquipo.MAQUINA, Musculo.ISQUIOTIBIALES, Musculo.PANTORRILLAS),
        ejercicio("zancadas-mancuernas", "Zancadas (Mancuernas)", TipoCategoria.PIERNA, TipoEquipo.PESA, Musculo.CUADRICEPS, Musculo.GLUTEOS),
        ejercicio("puente-gluteo", "Puente de glúteo", TipoCategoria.PIERNA, TipoEquipo.TAPETE, Musculo.GLUTEOS, Musculo.ISQUIOTIBIALES),
        ejercicio("remo-barra", "Remo inclinado (Barra)", TipoCategoria.ESPALDA, TipoEquipo.BARRA, Musculo.DORSALES, Musculo.BICEPS),
        ejercicio("jalon-polea", "Jalón al pecho (Máquina)", TipoCategoria.ESPALDA, TipoEquipo.MAQUINA, Musculo.DORSALES, Musculo.BICEPS),
        ejercicio("peso-muerto", "Peso muerto (Barra)", TipoCategoria.FUERZA, TipoEquipo.BARRA, Musculo.LUMBARES, Musculo.GLUTEOS, Musculo.ISQUIOTIBIALES),
        ejercicio("press-militar", "Press militar (Barra)", TipoCategoria.FUERZA, TipoEquipo.BARRA, Musculo.HOMBROS, Musculo.TRICEPS),
        ejercicio("curl-biceps", "Curl de bíceps (Mancuernas)", TipoCategoria.BRAZOS, TipoEquipo.PESA, Musculo.BICEPS, Musculo.ANTEBRAZOS),
        ejercicio("extension-triceps", "Extensión de tríceps en polea", TipoCategoria.BRAZOS, TipoEquipo.MAQUINA, Musculo.TRICEPS),
        ejercicio("plancha", "Plancha", TipoCategoria.ABDOMEN, TipoEquipo.TAPETE, Musculo.ABDOMINALES, Musculo.HOMBROS),
        ejercicio("crunch", "Crunch abdominal", TipoCategoria.ABDOMEN, TipoEquipo.TAPETE, Musculo.ABDOMINALES),
        ejercicio("elevaciones-laterales", "Elevaciones laterales (Mancuernas)", TipoCategoria.HOMBROS, TipoEquipo.PESA, Musculo.HOMBROS),
        ejercicio("remo-maquina", "Remo en máquina", TipoCategoria.CARDIO, TipoEquipo.MAQUINA, Musculo.DORSALES, Musculo.CUADRICEPS),
        ejercicio("burpees", "Burpees", TipoCategoria.CARDIO, TipoEquipo.TAPETE, Musculo.CUADRICEPS, Musculo.PECHO),
    )

    private fun porId(id: String): Ejercicio = ejercicios.first { it.id == id }

    /** [peso] y [reps] por serie normal; [calentamientos] son pares (peso, reps). */
    private fun ejercicioPlan(
        id: String,
        peso: Double?,
        reps: List<Int>,
        calentamientos: List<Pair<Double?, Int>> = emptyList(),
    ): EjercicioPlan {
        val normales = reps.mapIndexed { i, r -> Serie(id = i + 1, pesoKg = peso, repeticiones = r) }
        val calentamiento = calentamientos.mapIndexed { i, (p, r) ->
            Serie(id = reps.size + i + 1, tipo = TipoSerie.CALENTAMIENTO, pesoKg = p, repeticiones = r)
        }
        return EjercicioPlan(porId(id), normales + calentamiento)
    }

    private val cuatroDeDoce = listOf(12, 12, 12, 12)

    val planes: List<Plan> = listOf(
        Plan(
            id = "plan-lunes", nombre = "Set del Lunes", categoria = TipoCategoria.PECHO,
            imagenUrl = imagen(TipoCategoria.PECHO), fecha = LocalDate(2026, 10, 12),
            ejercicios = listOf(
                ejercicioPlan("press-banca-barra", 40.0, cuatroDeDoce),
                ejercicioPlan("press-inclinado-barra", 30.0, cuatroDeDoce),
                ejercicioPlan("aperturas-polea", 15.0, cuatroDeDoce),
                ejercicioPlan("fondos-pecho", null, cuatroDeDoce),
                ejercicioPlan("press-banca-mancuernas", 14.0, cuatroDeDoce),
                ejercicioPlan("aperturas-mancuernas", 10.0, cuatroDeDoce),
            ),
        ),
        Plan(
            id = "plan-martes", nombre = "Set del Martes", categoria = TipoCategoria.BRAZOS,
            imagenUrl = imagen(TipoCategoria.BRAZOS), fecha = LocalDate(2026, 10, 13),
            ejercicios = listOf(
                ejercicioPlan("curl-biceps", 10.0, cuatroDeDoce),
                ejercicioPlan("extension-triceps", 20.0, cuatroDeDoce),
                ejercicioPlan("press-militar", 25.0, listOf(10, 10, 8, 8)),
                ejercicioPlan("elevaciones-laterales", 6.0, cuatroDeDoce),
            ),
        ),
        Plan(
            id = "plan-miercoles", nombre = "Set del Miércoles", categoria = TipoCategoria.PIERNA,
            imagenUrl = imagen(TipoCategoria.PIERNA), fecha = LocalDate(2026, 10, 16),
            ejercicios = listOf(
                // Igual que el diseño "Ejercicio en progreso": 4 series + 2 de calentamiento.
                ejercicioPlan("sentadilla-barra", 10.0, listOf(12, 12, 15, 15), listOf(null to 10, 5.0 to 10)),
                ejercicioPlan("curl-piernas-acostado", 25.0, cuatroDeDoce),
                ejercicioPlan("zancadas-mancuernas", 8.0, listOf(10, 10, 10)),
                ejercicioPlan("puente-gluteo", null, listOf(15, 15, 15)),
            ),
        ),
        Plan(
            id = "plan-sabado", nombre = "Set del Sábado", categoria = TipoCategoria.ESPALDA,
            imagenUrl = imagen(TipoCategoria.ESPALDA), fecha = LocalDate(2026, 10, 17),
            ejercicios = listOf(
                ejercicioPlan("remo-barra", 35.0, cuatroDeDoce),
                ejercicioPlan("jalon-polea", 40.0, cuatroDeDoce),
                ejercicioPlan("peso-muerto", 60.0, listOf(8, 8, 6, 6)),
                ejercicioPlan("plancha", null, listOf(1, 1, 1)),
            ),
        ),
    )

    // ------------------------------------------------------------------ cuenta demo

    /** Cuenta de demostración con el cuestionario ya completo (entra directo a la app). */
    const val CORREO_DEMO = "checo@velok.app"
    const val CONTRASENA_DEMO = "velok2026"
    const val CORREO_GOOGLE = "checo.google@gmail.com"

    val usuarioDemo = Usuario(
        id = "usuario-demo",
        nombre = "Checo",
        correo = CORREO_DEMO,
        perfil = PerfilFisico(
            fechaNacimiento = LocalDate(2004, 4, 25),
            unidadPeso = UnidadPeso.KG,
            estaturaCm = 170,
            pesoKg = 100.0,
            meta = MetaEntrenamiento.PERDER_GRASA,
        ),
    )

    // ------------------------------------------------------------------ historial

    private const val DIAS_DE_HISTORIAL = 42
    private const val PROBABILIDAD_DE_ENTRENAR = 0.75
    private const val INCREMENTO_KG_POR_SEMANA = 1.25
    private const val VARIACION_KG = 2.5

    // Cada plan de ejemplo se entrena el día de la semana de su nombre.
    private val planPorDia = mapOf(
        DayOfWeek.MONDAY to "plan-lunes",
        DayOfWeek.TUESDAY to "plan-martes",
        DayOfWeek.WEDNESDAY to "plan-miercoles",
        DayOfWeek.SATURDAY to "plan-sabado",
    )

    /**
     * Sesiones de las últimas 6 semanas para que las gráficas de progreso tengan datos.
     * Es determinista (semilla fija) y las fechas son relativas a hoy.
     */
    fun historial(clock: Clock): List<SesionEntrenamiento> {
        val zona = TimeZone.currentSystemDefault()
        val hoy = clock.todayIn(zona)
        val aleatorio = Random(seed = 7)
        return (DIAS_DE_HISTORIAL downTo 1).mapNotNull { diasAtras ->
            val fecha = hoy.minus(diasAtras, DateTimeUnit.DAY)
            val plan = planes.firstOrNull { it.id == planPorDia[fecha.dayOfWeek] } ?: return@mapNotNull null
            if (aleatorio.nextDouble() > PROBABILIDAD_DE_ENTRENAR) return@mapNotNull null

            // El peso sube poco a poco (en el pasado se levantaba menos que en el plan actual),
            // con días mejores y peores para que la gráfica no sea una línea recta.
            val semanasAtras = diasAtras / 7
            val variacion = aleatorio.nextInt(-1, 2) * VARIACION_KG
            val inicio = fecha.atTime(LocalTime(18, 0)).toInstant(zona)
            val sesion = plan.iniciarSesion(id = "historial-$diasAtras", inicio = inicio)
            sesion.copy(
                fin = inicio + aleatorio.nextInt(30, 50).minutes,
                ejercicios = sesion.ejercicios.map { ej ->
                    ej.copy(series = ej.series.map { serie ->
                        val peso = serie.pesoKg?.let {
                            (it - semanasAtras * INCREMENTO_KG_POR_SEMANA + variacion).coerceAtLeast(0.0)
                        }
                        serie.copy(pesoKg = peso, completada = true)
                    })
                },
            )
        }
    }
}
