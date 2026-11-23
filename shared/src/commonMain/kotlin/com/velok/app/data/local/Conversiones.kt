package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.EjercicioSesion
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.OrigenEjercicio
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.SerieSesion
import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.Usuario
import com.velok.app.domain.model.normalizarBusqueda
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

// Conversión entre las tablas (Entidades.kt) y los modelos del dominio.

/**
 * Las imágenes locales no se guardan: son recursos cuya ruta en iOS cambia al reinstalar la app,
 * así que se calculan a partir de la categoría. Solo se guardan URLs remotas (ExerciseDB).
 */
private fun imagen(categoria: TipoCategoria): String = DatosMock.imagen(categoria)

private fun String.esUrlRemota() = startsWith("https://") || startsWith("http://")

private fun idHijo(padreId: String, posicion: Int) = "$padreId:$posicion"

// ---------------------------------------------------------------- ejercicios

fun Ejercicio.aEntidad(orden: Int) = EjercicioEntidad(
    id = id,
    orden = orden,
    nombre = nombre,
    categoria = categoria.name,
    equipo = equipo.name,
    musculoPrimario = musculoPrimario.name,
    musculosSecundarios = musculosSecundarios.joinToString(",") { it.name },
    seriesSugeridas = seriesSugeridas,
    repeticionesSugeridas = repeticionesSugeridas,
    imagenUrl = imagenUrl.takeIf { it.esUrlRemota() },
    origen = origen.name,
    nombreBusqueda = normalizarBusqueda(nombre),
)

fun EjercicioEntidad.aDominio(): Ejercicio {
    val tipo = TipoCategoria.valueOf(categoria)
    return Ejercicio(
        id = id,
        nombre = nombre,
        categoria = tipo,
        equipo = TipoEquipo.valueOf(equipo),
        musculoPrimario = Musculo.valueOf(musculoPrimario),
        musculosSecundarios = musculosSecundarios.split(",").filter { it.isNotEmpty() }.map(Musculo::valueOf),
        imagenUrl = imagenUrl ?: imagen(tipo),
        seriesSugeridas = seriesSugeridas,
        repeticionesSugeridas = repeticionesSugeridas,
        origen = OrigenEjercicio.valueOf(origen),
    )
}

// ---------------------------------------------------------------- planes

/** Filas que representan un plan completo. */
class FilasPlan(
    val plan: PlanEntidad,
    val ejercicios: List<PlanEjercicioEntidad>,
    val series: List<SeriePlanEntidad>,
)

fun Plan.aFilas(orden: Int): FilasPlan {
    val ejerciciosFilas = ejercicios.mapIndexed { posicion, ep ->
        PlanEjercicioEntidad(
            id = idHijo(id, posicion),
            planId = id,
            posicion = posicion,
            ejercicioId = ep.ejercicio.id,
            descansoSegundos = ep.descansoSegundos,
            nota = ep.nota,
        )
    }
    val seriesFilas = ejercicios.flatMapIndexed { posicion, ep ->
        ep.series.mapIndexed { i, serie ->
            SeriePlanEntidad(
                planEjercicioId = idHijo(id, posicion),
                id = serie.id,
                posicion = i,
                tipo = serie.tipo.name,
                pesoKg = serie.pesoKg,
                repeticiones = serie.repeticiones,
            )
        }
    }
    return FilasPlan(
        plan = PlanEntidad(id = id, orden = orden, nombre = nombre, categoria = categoria.name, fecha = fecha.toString()),
        ejercicios = ejerciciosFilas,
        series = seriesFilas,
    )
}

fun PlanCompleto.aDominio(): Plan {
    val categoria = TipoCategoria.valueOf(plan.categoria)
    return Plan(
        id = plan.id,
        nombre = plan.nombre,
        categoria = categoria,
        imagenUrl = imagen(categoria),
        fecha = LocalDate.parse(plan.fecha),
        ejercicios = ejercicios.sortedBy { it.datos.posicion }.map { ep ->
            EjercicioPlan(
                ejercicio = ep.ejercicio.aDominio(),
                series = ep.series.sortedBy { it.posicion }.map { it.aDominio() },
                descansoSegundos = ep.datos.descansoSegundos,
                nota = ep.datos.nota,
            )
        },
    )
}

private fun SeriePlanEntidad.aDominio() =
    Serie(id = id, tipo = TipoSerie.valueOf(tipo), pesoKg = pesoKg, repeticiones = repeticiones)

// ---------------------------------------------------------------- sesiones

class FilasSesion(
    val sesion: SesionEntidad,
    val ejercicios: List<SesionEjercicioEntidad>,
    val series: List<SerieSesionEntidad>,
)

fun SesionEntrenamiento.aFilas(): FilasSesion {
    val ejerciciosFilas = ejercicios.mapIndexed { posicion, ej ->
        SesionEjercicioEntidad(
            id = idHijo(id, posicion),
            sesionId = id,
            posicion = posicion,
            ejercicioId = ej.ejercicio.id,
            descansoSegundos = ej.descansoSegundos,
            nota = ej.nota,
        )
    }
    val seriesFilas = ejercicios.flatMapIndexed { posicion, ej ->
        ej.series.mapIndexed { i, serie ->
            SerieSesionEntidad(
                sesionEjercicioId = idHijo(id, posicion),
                id = serie.id,
                posicion = i,
                tipo = serie.tipo.name,
                pesoKg = serie.pesoKg,
                repeticiones = serie.repeticiones,
                completada = serie.completada,
                anteriorId = serie.anterior?.id,
                anteriorTipo = serie.anterior?.tipo?.name,
                anteriorPesoKg = serie.anterior?.pesoKg,
                anteriorRepeticiones = serie.anterior?.repeticiones,
            )
        }
    }
    return FilasSesion(
        sesion = SesionEntidad(
            id = id,
            planId = planId,
            nombrePlan = nombrePlan,
            inicioMs = inicio.toEpochMilliseconds(),
            finMs = fin?.toEpochMilliseconds(),
        ),
        ejercicios = ejerciciosFilas,
        series = seriesFilas,
    )
}

fun SesionCompleta.aDominio() = SesionEntrenamiento(
    id = sesion.id,
    planId = sesion.planId,
    nombrePlan = sesion.nombrePlan,
    inicio = Instant.fromEpochMilliseconds(sesion.inicioMs),
    fin = sesion.finMs?.let(Instant::fromEpochMilliseconds),
    ejercicios = ejercicios.sortedBy { it.datos.posicion }.map { ej ->
        EjercicioSesion(
            ejercicio = ej.ejercicio.aDominio(),
            descansoSegundos = ej.datos.descansoSegundos,
            nota = ej.datos.nota,
            series = ej.series.sortedBy { it.posicion }.map { it.aDominio() },
        )
    },
)

private fun SerieSesionEntidad.aDominio() = SerieSesion(
    id = id,
    tipo = TipoSerie.valueOf(tipo),
    anterior = anteriorId?.let { anteriorId ->
        Serie(
            id = anteriorId,
            tipo = TipoSerie.valueOf(requireNotNull(anteriorTipo)),
            pesoKg = anteriorPesoKg,
            repeticiones = anteriorRepeticiones,
        )
    },
    pesoKg = pesoKg,
    repeticiones = repeticiones,
    completada = completada,
)

// ---------------------------------------------------------------- cuentas

fun CuentaEntidad.aDominio(): Usuario {
    // El perfil existe solo si están todas sus columnas (se guardan juntas al terminar el cuestionario).
    val perfil = if (fechaNacimiento != null && unidadPeso != null && estaturaCm != null && pesoKg != null && meta != null) {
        PerfilFisico(
            fechaNacimiento = LocalDate.parse(fechaNacimiento),
            unidadPeso = UnidadPeso.valueOf(unidadPeso),
            estaturaCm = estaturaCm,
            pesoKg = pesoKg,
            meta = MetaEntrenamiento.valueOf(meta),
        )
    } else {
        null
    }
    return Usuario(id = id, nombre = nombre, correo = correo, fotoUrl = fotoUrl, perfil = perfil)
}

fun Usuario.aEntidad(hashContrasena: String) = CuentaEntidad(
    id = id,
    nombre = nombre,
    correo = correo,
    hashContrasena = hashContrasena,
    fotoUrl = fotoUrl,
    fechaNacimiento = perfil?.fechaNacimiento?.toString(),
    unidadPeso = perfil?.unidadPeso?.name,
    estaturaCm = perfil?.estaturaCm,
    pesoKg = perfil?.pesoKg,
    meta = perfil?.meta?.name,
)
