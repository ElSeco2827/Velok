package com.velok.app.presentation.util

import androidx.compose.runtime.Composable
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.GrupoMuscular
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoMetrica
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToLong
import kotlin.time.Duration
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.categoria_abdomen
import velok.shared.generated.resources.categoria_brazos
import velok.shared.generated.resources.categoria_cardio
import velok.shared.generated.resources.categoria_espalda
import velok.shared.generated.resources.categoria_fuerza
import velok.shared.generated.resources.categoria_hombros
import velok.shared.generated.resources.categoria_pecho
import velok.shared.generated.resources.categoria_pierna
import velok.shared.generated.resources.duracion_horas
import velok.shared.generated.resources.duracion_minutos
import velok.shared.generated.resources.duracion_segundos
import velok.shared.generated.resources.ejercicio_series
import velok.shared.generated.resources.equipo_barra
import velok.shared.generated.resources.equipo_maquina
import velok.shared.generated.resources.equipo_pesa
import velok.shared.generated.resources.equipo_tapete
import velok.shared.generated.resources.fecha_completa
import velok.shared.generated.resources.fecha_corta
import velok.shared.generated.resources.grupo_brazos
import velok.shared.generated.resources.grupo_core
import velok.shared.generated.resources.grupo_espalda
import velok.shared.generated.resources.grupo_hombros
import velok.shared.generated.resources.grupo_pecho
import velok.shared.generated.resources.grupo_piernas
import velok.shared.generated.resources.ic_calorias
import velok.shared.generated.resources.ic_categoria_abdomen
import velok.shared.generated.resources.ic_categoria_brazos
import velok.shared.generated.resources.ic_categoria_espalda
import velok.shared.generated.resources.ic_categoria_hombros
import velok.shared.generated.resources.ic_categoria_pecho
import velok.shared.generated.resources.ic_categoria_pierna
import velok.shared.generated.resources.ic_distancia
import velok.shared.generated.resources.ic_ejercicio
import velok.shared.generated.resources.ic_equipo_barra
import velok.shared.generated.resources.ic_equipo_maquina
import velok.shared.generated.resources.ic_equipo_tapete
import velok.shared.generated.resources.ic_pasos
import velok.shared.generated.resources.ic_ritmo_cardiaco
import velok.shared.generated.resources.meses
import velok.shared.generated.resources.meses_cortos
import velok.shared.generated.resources.metrica_calorias
import velok.shared.generated.resources.metrica_distancia
import velok.shared.generated.resources.metrica_pasos
import velok.shared.generated.resources.metrica_peso
import velok.shared.generated.resources.metrica_repeticiones
import velok.shared.generated.resources.metrica_ritmo_cardiaco
import velok.shared.generated.resources.metrica_volumen
import velok.shared.generated.resources.musculo_abdominales
import velok.shared.generated.resources.musculo_antebrazos
import velok.shared.generated.resources.musculo_biceps
import velok.shared.generated.resources.musculo_cuadriceps
import velok.shared.generated.resources.musculo_dorsales
import velok.shared.generated.resources.musculo_gluteos
import velok.shared.generated.resources.musculo_hombros
import velok.shared.generated.resources.musculo_isquiotibiales
import velok.shared.generated.resources.musculo_lumbares
import velok.shared.generated.resources.musculo_pantorrillas
import velok.shared.generated.resources.musculo_pecho
import velok.shared.generated.resources.musculo_triceps
import velok.shared.generated.resources.plan_sin_nombre
import velok.shared.generated.resources.rango_mes
import velok.shared.generated.resources.rango_semana
import velok.shared.generated.resources.rango_tres_dias
import velok.shared.generated.resources.rango_tres_meses
import velok.shared.generated.resources.valor_calorias
import velok.shared.generated.resources.valor_distancia
import velok.shared.generated.resources.valor_pasos
import velok.shared.generated.resources.valor_ritmo_cardiaco

// Traduce los modelos de dominio (sin textos ni recursos) a recursos de UI.

val TipoCategoria.nombre: StringResource
    get() = when (this) {
        TipoCategoria.PECHO -> Res.string.categoria_pecho
        TipoCategoria.FUERZA -> Res.string.categoria_fuerza
        TipoCategoria.PIERNA -> Res.string.categoria_pierna
        TipoCategoria.ESPALDA -> Res.string.categoria_espalda
        TipoCategoria.BRAZOS -> Res.string.categoria_brazos
        TipoCategoria.ABDOMEN -> Res.string.categoria_abdomen
        TipoCategoria.HOMBROS -> Res.string.categoria_hombros
        TipoCategoria.CARDIO -> Res.string.categoria_cardio
    }

val TipoCategoria.icono: DrawableResource
    get() = when (this) {
        TipoCategoria.PECHO -> Res.drawable.ic_categoria_pecho
        TipoCategoria.FUERZA -> Res.drawable.ic_ejercicio
        TipoCategoria.PIERNA -> Res.drawable.ic_categoria_pierna
        TipoCategoria.ESPALDA -> Res.drawable.ic_categoria_espalda
        TipoCategoria.BRAZOS -> Res.drawable.ic_categoria_brazos
        TipoCategoria.ABDOMEN -> Res.drawable.ic_categoria_abdomen
        TipoCategoria.HOMBROS -> Res.drawable.ic_categoria_hombros
        TipoCategoria.CARDIO -> Res.drawable.ic_ritmo_cardiaco
    }

val TipoEquipo.nombre: StringResource
    get() = when (this) {
        TipoEquipo.BARRA -> Res.string.equipo_barra
        TipoEquipo.PESA -> Res.string.equipo_pesa
        TipoEquipo.TAPETE -> Res.string.equipo_tapete
        TipoEquipo.MAQUINA -> Res.string.equipo_maquina
    }

val TipoEquipo.icono: DrawableResource
    get() = when (this) {
        TipoEquipo.BARRA -> Res.drawable.ic_equipo_barra
        TipoEquipo.PESA -> Res.drawable.ic_ejercicio
        TipoEquipo.TAPETE -> Res.drawable.ic_equipo_tapete
        TipoEquipo.MAQUINA -> Res.drawable.ic_equipo_maquina
    }

val Musculo.nombre: StringResource
    get() = when (this) {
        Musculo.PECHO -> Res.string.musculo_pecho
        Musculo.DORSALES -> Res.string.musculo_dorsales
        Musculo.LUMBARES -> Res.string.musculo_lumbares
        Musculo.HOMBROS -> Res.string.musculo_hombros
        Musculo.BICEPS -> Res.string.musculo_biceps
        Musculo.TRICEPS -> Res.string.musculo_triceps
        Musculo.ANTEBRAZOS -> Res.string.musculo_antebrazos
        Musculo.ABDOMINALES -> Res.string.musculo_abdominales
        Musculo.CUADRICEPS -> Res.string.musculo_cuadriceps
        Musculo.ISQUIOTIBIALES -> Res.string.musculo_isquiotibiales
        Musculo.GLUTEOS -> Res.string.musculo_gluteos
        Musculo.PANTORRILLAS -> Res.string.musculo_pantorrillas
    }

val GrupoMuscular.nombre: StringResource
    get() = when (this) {
        GrupoMuscular.ESPALDA -> Res.string.grupo_espalda
        GrupoMuscular.PECHO -> Res.string.grupo_pecho
        GrupoMuscular.CORE -> Res.string.grupo_core
        GrupoMuscular.HOMBROS -> Res.string.grupo_hombros
        GrupoMuscular.BRAZOS -> Res.string.grupo_brazos
        GrupoMuscular.PIERNAS -> Res.string.grupo_piernas
    }

val MetricaProgreso.nombre: StringResource
    get() = when (this) {
        MetricaProgreso.PESO -> Res.string.metrica_peso
        MetricaProgreso.VOLUMEN -> Res.string.metrica_volumen
        MetricaProgreso.REPETICIONES -> Res.string.metrica_repeticiones
    }

val RangoTiempo.nombre: StringResource
    get() = when (this) {
        RangoTiempo.TRES_DIAS -> Res.string.rango_tres_dias
        RangoTiempo.SEMANA -> Res.string.rango_semana
        RangoTiempo.MES -> Res.string.rango_mes
        RangoTiempo.TRES_MESES -> Res.string.rango_tres_meses
    }

val TipoMetrica.etiqueta: StringResource
    get() = when (this) {
        TipoMetrica.CALORIAS -> Res.string.metrica_calorias
        TipoMetrica.PASOS -> Res.string.metrica_pasos
        TipoMetrica.DISTANCIA -> Res.string.metrica_distancia
        TipoMetrica.RITMO_CARDIACO -> Res.string.metrica_ritmo_cardiaco
    }

val TipoMetrica.icono: DrawableResource
    get() = when (this) {
        TipoMetrica.CALORIAS -> Res.drawable.ic_calorias
        TipoMetrica.PASOS -> Res.drawable.ic_pasos
        TipoMetrica.DISTANCIA -> Res.drawable.ic_distancia
        TipoMetrica.RITMO_CARDIACO -> Res.drawable.ic_ritmo_cardiaco
    }

/** Valor de la métrica con su unidad: "160 kcal", "2,032", "1.5 km", "90 lpm". */
@Composable
fun Metrica.valorFormateado(): String = when (tipo) {
    TipoMetrica.CALORIAS -> stringResource(Res.string.valor_calorias, formatearMiles(valor.roundToLong()))
    TipoMetrica.PASOS -> stringResource(Res.string.valor_pasos, formatearMiles(valor.roundToLong()))
    TipoMetrica.DISTANCIA -> stringResource(Res.string.valor_distancia, formatearDecimal(valor, 1))
    TipoMetrica.RITMO_CARDIACO -> stringResource(Res.string.valor_ritmo_cardiaco, formatearMiles(valor.roundToLong()))
}

/** Nombre del plan o "Plan sin nombre" si el usuario no le puso uno. */
@Composable
fun Plan.nombreVisible(): String = nombre.ifBlank { stringResource(Res.string.plan_sin_nombre) }

/** "12 × 4 series" con lo sugerido para el ejercicio. */
@Composable
fun Ejercicio.resumenSeries(): String =
    stringResource(Res.string.ejercicio_series, repeticionesSugeridas, seriesSugeridas)

/** "12 × 4 series" con lo planeado: repeticiones de la primera serie normal × número de series normales. */
@Composable
fun EjercicioPlan.resumenSeries(): String {
    val normales = seriesNormales
    val repeticiones = normales.firstOrNull()?.repeticiones ?: 0
    return stringResource(Res.string.ejercicio_series, repeticiones, normales.size)
}

/** Fecha en español: "12 Octubre 2026". */
@Composable
fun LocalDate.formatoLargo(): String {
    val meses = stringArrayResource(Res.array.meses)
    return stringResource(Res.string.fecha_completa, day, meses[month.ordinal], year)
}

/** Fecha corta para ejes de gráficas: "26 jul". */
@Composable
fun LocalDate.formatoCorto(): String {
    val meses = stringArrayResource(Res.array.meses_cortos)
    return stringResource(Res.string.fecha_corta, day, meses[month.ordinal])
}

/** "12s", "1min 0s" o "1h 5min". */
@Composable
fun Duration.formatoCronometro(): String {
    val total = inWholeSeconds.coerceAtLeast(0)
    val horas = (total / 3600).toInt()
    val minutos = ((total % 3600) / 60).toInt()
    val segundos = (total % 60).toInt()
    return when {
        horas > 0 -> stringResource(Res.string.duracion_horas, horas, minutos)
        minutos > 0 -> stringResource(Res.string.duracion_minutos, minutos, segundos)
        else -> stringResource(Res.string.duracion_segundos, segundos)
    }
}
