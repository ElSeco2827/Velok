package com.velok.app.presentation.util

import androidx.compose.runtime.Composable
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoMetrica
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToLong
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.categoria_abdomen
import velok.shared.generated.resources.categoria_brazos
import velok.shared.generated.resources.categoria_cardio
import velok.shared.generated.resources.categoria_espalda
import velok.shared.generated.resources.categoria_fuerza
import velok.shared.generated.resources.categoria_hombros
import velok.shared.generated.resources.categoria_pecho
import velok.shared.generated.resources.categoria_pierna
import velok.shared.generated.resources.fecha_completa
import velok.shared.generated.resources.ic_calorias
import velok.shared.generated.resources.ic_categoria_abdomen
import velok.shared.generated.resources.ic_categoria_brazos
import velok.shared.generated.resources.ic_categoria_espalda
import velok.shared.generated.resources.ic_categoria_hombros
import velok.shared.generated.resources.ic_categoria_pecho
import velok.shared.generated.resources.ic_categoria_pierna
import velok.shared.generated.resources.ic_distancia
import velok.shared.generated.resources.ic_ejercicio
import velok.shared.generated.resources.ic_pasos
import velok.shared.generated.resources.ic_ritmo_cardiaco
import velok.shared.generated.resources.meses
import velok.shared.generated.resources.metrica_calorias
import velok.shared.generated.resources.metrica_distancia
import velok.shared.generated.resources.metrica_pasos
import velok.shared.generated.resources.metrica_ritmo_cardiaco
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

/** Fecha en español: "12 Octubre 2026". */
@Composable
fun LocalDate.formatoLargo(): String {
    val meses = stringArrayResource(Res.array.meses)
    return stringResource(Res.string.fecha_completa, day, meses[month.ordinal], year)
}
