package com.velok.app.domain.model

// Reglas para editar un plan. Son funciones puras: devuelven una copia nueva y no modifican nada,
// así se pueden probar sin repositorios ni UI.

fun Plan.renombrar(nombre: String): Plan = copy(nombre = nombre)

/**
 * La categoría del plan es la más repetida entre sus ejercicios (en empate, la del primero que
 * aparece) y su imagen es la del primer ejercicio de esa categoría. Sin ejercicios no cambia.
 */
fun Plan.conCategoriaInferida(): Plan {
    if (ejercicios.isEmpty()) return this
    val conteo = ejercicios.groupingBy { it.ejercicio.categoria }.eachCount()
    val maximo = conteo.values.max()
    val categoria = ejercicios.first { conteo[it.ejercicio.categoria] == maximo }.ejercicio.categoria
    val imagen = ejercicios.first { it.ejercicio.categoria == categoria }.ejercicio.imagenUrl
    return copy(categoria = categoria, imagenUrl = imagen)
}

/** Agrega el ejercicio al final con sus series sugeridas (sin peso definido). */
fun Plan.agregarEjercicio(ejercicio: Ejercicio): Plan {
    val series = (1..ejercicio.seriesSugeridas).map { id ->
        Serie(id = id, repeticiones = ejercicio.repeticionesSugeridas)
    }
    return copy(ejercicios = ejercicios + EjercicioPlan(ejercicio, series))
}

fun Plan.eliminarEjercicio(indice: Int): Plan =
    copy(ejercicios = ejercicios.filterIndexed { i, _ -> i != indice })

/** Agrega una serie normal que copia los valores de la última, como al anotar en papel. */
fun Plan.agregarSerie(indiceEjercicio: Int): Plan = actualizarEjercicio(indiceEjercicio) { ep ->
    val ultima = ep.series.lastOrNull()
    val nueva = Serie(
        id = (ep.series.maxOfOrNull { it.id } ?: 0) + 1,
        pesoKg = ultima?.pesoKg,
        repeticiones = ultima?.repeticiones,
    )
    ep.copy(series = ep.series + nueva)
}

fun Plan.actualizarSerie(indiceEjercicio: Int, idSerie: Int, pesoKg: Double?, repeticiones: Int?): Plan =
    actualizarEjercicio(indiceEjercicio) { ep ->
        ep.copy(series = ep.series.map {
            if (it.id == idSerie) it.copy(pesoKg = pesoKg, repeticiones = repeticiones) else it
        })
    }

/** Cambia una serie entre normal y calentamiento. */
fun Plan.alternarTipoSerie(indiceEjercicio: Int, idSerie: Int): Plan = actualizarEjercicio(indiceEjercicio) { ep ->
    ep.copy(series = ep.series.map {
        if (it.id != idSerie) it
        else it.copy(tipo = if (it.tipo == TipoSerie.NORMAL) TipoSerie.CALENTAMIENTO else TipoSerie.NORMAL)
    })
}

fun Plan.cambiarDescanso(indiceEjercicio: Int, segundos: Int): Plan =
    actualizarEjercicio(indiceEjercicio) { it.copy(descansoSegundos = segundos.coerceAtLeast(0)) }

fun Plan.cambiarNota(indiceEjercicio: Int, nota: String): Plan =
    actualizarEjercicio(indiceEjercicio) { it.copy(nota = nota) }

private inline fun Plan.actualizarEjercicio(indice: Int, cambio: (EjercicioPlan) -> EjercicioPlan): Plan {
    if (indice !in ejercicios.indices) return this
    return copy(ejercicios = ejercicios.mapIndexed { i, ep -> if (i == indice) cambio(ep) else ep })
}
