package com.velok.app.presentation.util

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.FiltroCatalogo
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.normalizarBusqueda
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.resources.getString

/** Espera después de la última tecla antes de consultar la base de datos. */
const val ESPERA_BUSQUEDA_MS = 300L

/** Con menos letras, buscar por músculo o categoría daría demasiadas coincidencias ("a", "pe"). */
private const val MINIMO_PARA_TRADUCIR = 3

/**
 * Búsqueda del catálogo compartida por la pestaña Ejercicio y la hoja "Agregar ejercicio".
 *
 * Los nombres de los ejercicios están en inglés, pero los músculos y categorías se muestran en
 * español: si lo escrito aparece en el nombre en español de un músculo o categoría ("pecho",
 * "bíceps"), también se buscan esos. Esto vive en la presentación porque los textos en español son
 * recursos de la UI (strings.xml), que el dominio no conoce.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun ObtenerEjerciciosUseCase.buscar(
    texto: Flow<String>,
    equipo: Flow<TipoEquipo?>,
): Flow<List<Ejercicio>> {
    val nombres = NombresEnEspanol()
    val textoEstable = texto
        .debounce { if (it.isBlank()) 0L else ESPERA_BUSQUEDA_MS }
        .distinctUntilChanged()
    return combine(textoEstable, equipo.distinctUntilChanged()) { t, e -> t to e }
        .mapLatest { (t, e) -> nombres.filtro(t, e) }
        .flatMapLatest { filtro -> this(filtro) }
}

/** Nombres en español de músculos y categorías, normalizados; se leen una vez de los recursos. */
private class NombresEnEspanol {
    private var musculos: Map<Musculo, String>? = null
    private var categorias: Map<TipoCategoria, String>? = null

    suspend fun filtro(texto: String, equipo: TipoEquipo?): FiltroCatalogo {
        val buscado = normalizarBusqueda(texto)
        if (buscado.length < MINIMO_PARA_TRADUCIR) return FiltroCatalogo(texto = texto, equipo = equipo)
        val musculos = musculos ?: Musculo.entries.associateWith { normalizarBusqueda(getString(it.nombre)) }.also { musculos = it }
        val categorias = categorias ?: TipoCategoria.entries.associateWith { normalizarBusqueda(getString(it.nombre)) }.also { categorias = it }
        return FiltroCatalogo(
            texto = texto,
            musculosTexto = musculos.filterValues { buscado in it }.keys,
            categoriasTexto = categorias.filterValues { buscado in it }.keys,
            equipo = equipo,
        )
    }
}
