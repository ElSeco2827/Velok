package com.velok.app.domain.model

/**
 * Filtro del catálogo de ejercicios. Los filtros se combinan con "y"; dentro de la búsqueda por
 * texto, basta con que coincida una de sus partes.
 *
 * @property texto lo que escribió el usuario. Coincide si está dentro del nombre del ejercicio
 *   (sin distinguir mayúsculas ni acentos), o si el músculo principal está en [musculosTexto], o
 *   si la categoría está en [categoriasTexto]. Vacío = sin búsqueda.
 * @property musculosTexto músculos cuyo nombre (en español) contiene el texto buscado; los calcula
 *   la presentación porque los nombres en español son recursos de la UI.
 * @property categoriasTexto igual que [musculosTexto], para las categorías.
 * @property equipo null = cualquier equipo.
 * @property categoria null = cualquier categoría (pantalla de una categoría).
 */
data class FiltroCatalogo(
    val texto: String = "",
    val musculosTexto: Set<Musculo> = emptySet(),
    val categoriasTexto: Set<TipoCategoria> = emptySet(),
    val equipo: TipoEquipo? = null,
    val categoria: TipoCategoria? = null,
)

private val sinAcento = mapOf(
    'á' to 'a', 'à' to 'a', 'ä' to 'a', 'â' to 'a', 'ã' to 'a',
    'é' to 'e', 'è' to 'e', 'ë' to 'e', 'ê' to 'e',
    'í' to 'i', 'ì' to 'i', 'ï' to 'i', 'î' to 'i',
    'ó' to 'o', 'ò' to 'o', 'ö' to 'o', 'ô' to 'o', 'õ' to 'o',
    'ú' to 'u', 'ù' to 'u', 'ü' to 'u', 'û' to 'u',
    'ñ' to 'n', 'ç' to 'c',
)

/**
 * Texto para comparar en búsquedas: minúsculas, sin acentos y con los espacios de más quitados
 * ("  Elevación  LATERAL" -> "elevacion lateral"). Se aplica igual al nombre guardado y a lo que
 * escribe el usuario, así "elevacion", "ELEVACIÓN" y "Elevación" encuentran lo mismo.
 */
fun normalizarBusqueda(texto: String): String =
    texto.lowercase()
        .map { sinAcento[it] ?: it }
        .joinToString("")
        .trim()
        .replace(Regex("\\s+"), " ")
