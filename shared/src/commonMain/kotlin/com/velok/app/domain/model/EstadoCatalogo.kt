package com.velok.app.domain.model

/**
 * Estado del catálogo de ejercicios descargado. La app siempre muestra lo guardado en el
 * dispositivo; este estado solo sirve para avisar cuando no hay catálogo descargado.
 */
enum class EstadoCatalogo {
    /** Se está descargando por primera vez (mientras tanto se ven los ejercicios de ejemplo). */
    DESCARGANDO,

    /** Hay catálogo guardado (o la descarga no hace falta). */
    LISTO,

    /** La descarga falló y no hay ningún ejercicio descargado: solo se ven los de ejemplo. */
    SIN_CONEXION,
}
