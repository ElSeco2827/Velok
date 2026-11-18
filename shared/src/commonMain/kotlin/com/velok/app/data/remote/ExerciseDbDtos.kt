package com.velok.app.data.remote

import kotlinx.serialization.Serializable

// Forma de las respuestas de la API gratuita de ExerciseDB V1 (oss.exercisedb.dev), copiada de
// respuestas reales. Todos los campos tienen valor por defecto para que un campo faltante no
// rompa la lectura; los ejercicios sin id o sin nombre se descartan al convertir.

/** Respuesta de `GET /api/v1/exercises?limit=25&after=<cursor>`. */
@Serializable
data class RespuestaEjerciciosDto(
    val success: Boolean = false,
    val meta: MetaPaginaDto? = null,
    val data: List<EjercicioDto> = emptyList(),
)

/** Paginación por cursor: [nextCursor] se manda como `after` para pedir la siguiente página. */
@Serializable
data class MetaPaginaDto(
    val total: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
    val nextCursor: String? = null,
    val previousCursor: String? = null,
)

/** Ejercicio tal como lo manda la API (textos en inglés). */
@Serializable
data class EjercicioDto(
    val exerciseId: String = "",
    val name: String = "",
    /** GIF de 180x180 en static.exercisedb.dev. */
    val gifUrl: String? = null,
    val bodyParts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val targetMuscles: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
)
