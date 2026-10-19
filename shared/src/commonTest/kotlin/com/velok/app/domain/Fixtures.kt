package com.velok.app.domain

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import kotlinx.datetime.LocalDate

// Datos de prueba compartidos por los tests del dominio.

fun ejercicioDePrueba(
    id: String = "sentadilla",
    categoria: TipoCategoria = TipoCategoria.PIERNA,
    musculo: Musculo = Musculo.CUADRICEPS,
    series: Int = 4,
    repeticiones: Int = 12,
) = Ejercicio(
    id = id,
    nombre = id,
    categoria = categoria,
    equipo = TipoEquipo.BARRA,
    musculoPrimario = musculo,
    musculosSecundarios = emptyList(),
    imagenUrl = "img-$id",
    seriesSugeridas = series,
    repeticionesSugeridas = repeticiones,
)

fun planDePrueba(vararg ejercicios: EjercicioPlan) = Plan(
    id = "plan",
    nombre = "Plan",
    categoria = TipoCategoria.FUERZA,
    imagenUrl = "",
    fecha = LocalDate(2026, 10, 12),
    ejercicios = ejercicios.toList(),
)

fun ejercicioPlanDePrueba(ejercicio: Ejercicio = ejercicioDePrueba(), vararg series: Serie) =
    EjercicioPlan(ejercicio, series.toList())
