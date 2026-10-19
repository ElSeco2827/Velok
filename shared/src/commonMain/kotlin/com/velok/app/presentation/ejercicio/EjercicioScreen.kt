package com.velok.app.presentation.ejercicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.presentation.components.CategoriaCard
import com.velok.app.presentation.components.ChipEquipo
import com.velok.app.presentation.components.EjercicioFila
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoError
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.resumenSeries
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.sin_ejercicios
import velok.shared.generated.resources.tab_ejercicio
import velok.shared.generated.resources.tab_entrenamiento

@Composable
fun EjercicioScreen(
    onAbrirEjercicio: (String) -> Unit,
    onAbrirCategoria: (TipoCategoria) -> Unit,
    viewModel: EjercicioViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    EjercicioContenido(
        estado = estado,
        onSeleccionarPestana = viewModel::seleccionarPestana,
        onAlternarEquipo = viewModel::alternarEquipo,
        onAbrirEjercicio = onAbrirEjercicio,
        onAbrirCategoria = onAbrirCategoria,
        onReintentar = viewModel::cargar,
    )
}

@Composable
fun EjercicioContenido(
    estado: EjercicioUiState,
    onSeleccionarPestana: (PestanaEjercicio) -> Unit,
    onAlternarEquipo: (TipoEquipo) -> Unit,
    onAbrirEjercicio: (String) -> Unit,
    onAbrirCategoria: (TipoCategoria) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        PestanasEjercicio(seleccionada = estado.pestana, onSeleccionar = onSeleccionarPestana)
        when (estado.pestana) {
            PestanaEjercicio.EJERCICIO -> {
                FiltrosEquipo(seleccionado = estado.equipo, onAlternar = onAlternarEquipo)
                when {
                    estado.cargando -> EstadoCargando()
                    estado.error -> EstadoError(onReintentar)
                    estado.ejercicios.isEmpty() -> EstadoVacio(stringResource(Res.string.sin_ejercicios))
                    else -> ListaEjercicios(estado.ejercicios, onAbrirEjercicio)
                }
            }
            PestanaEjercicio.ENTRENAMIENTO -> when {
                estado.cargando -> EstadoCargando()
                estado.error -> EstadoError(onReintentar)
                else -> CuadriculaCategorias(estado.categorias, onAbrirCategoria)
            }
        }
    }
}

private val PestanaEjercicio.titulo: StringResource
    get() = when (this) {
        PestanaEjercicio.EJERCICIO -> Res.string.tab_ejercicio
        PestanaEjercicio.ENTRENAMIENTO -> Res.string.tab_entrenamiento
    }

@Composable
private fun PestanasEjercicio(
    seleccionada: PestanaEjercicio,
    onSeleccionar: (PestanaEjercicio) -> Unit,
) {
    val colores = VelokTheme.colores
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .selectableGroup(),
    ) {
        PestanaEjercicio.entries.forEach { pestana ->
            val activa = pestana == seleccionada
            Column(
                Modifier
                    .weight(1f)
                    .selectable(selected = activa, role = Role.Tab, onClick = { onSeleccionar(pestana) }),
            ) {
                Text(
                    text = stringResource(pestana.titulo),
                    style = VelokTheme.tipografia.titleSmall,
                    color = if (activa) colores.acento else colores.textoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .background(if (activa) colores.acento else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun FiltrosEquipo(
    seleccionado: TipoEquipo?,
    onAlternar: (TipoEquipo) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TipoEquipo.entries.forEach { equipo ->
            ChipEquipo(
                equipo = equipo,
                seleccionado = equipo == seleccionado,
                onClick = { onAlternar(equipo) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ListaEjercicios(
    ejercicios: List<Ejercicio>,
    onAbrirEjercicio: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(ejercicios, key = { it.id }) { ejercicio ->
            EjercicioFila(
                nombre = ejercicio.nombre,
                detalle = ejercicio.resumenSeries(),
                imagenUrl = ejercicio.imagenUrl,
                onClick = { onAbrirEjercicio(ejercicio.id) },
            )
        }
    }
}

// Proporción ancho/alto por posición para el efecto escalonado del diseño.
private val proporciones = listOf(0.87f, 0.66f, 0.62f, 0.87f)

@Composable
private fun CuadriculaCategorias(
    categorias: List<CategoriaEjercicio>,
    onAbrirCategoria: (TipoCategoria) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(categorias, key = { _, categoria -> categoria.id }) { indice, categoria ->
            CategoriaCard(
                categoria = categoria,
                onClick = { onAbrirCategoria(categoria.tipo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(proporciones[indice % proporciones.size]),
            )
        }
    }
}

@Preview
@Composable
private fun EjercicioContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            EjercicioContenido(
                estado = EjercicioUiState(
                    cargando = false,
                    equipo = TipoEquipo.BARRA,
                    ejercicios = listOf(
                        Ejercicio("1", "Press de banca plano (Barra)", TipoCategoria.PECHO, TipoEquipo.BARRA, Musculo.PECHO, emptyList(), ""),
                        Ejercicio("2", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA, Musculo.CUADRICEPS, emptyList(), ""),
                    ),
                ),
                onSeleccionarPestana = {},
                onAlternarEquipo = {},
                onAbrirEjercicio = {},
                onAbrirCategoria = {},
                onReintentar = {},
            )
        }
    }
}

@Preview
@Composable
private fun EntrenamientoContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            EjercicioContenido(
                estado = EjercicioUiState(
                    pestana = PestanaEjercicio.ENTRENAMIENTO,
                    cargando = false,
                    categorias = TipoCategoria.entries.map { CategoriaEjercicio(it.name, it, "") },
                ),
                onSeleccionarPestana = {},
                onAlternarEquipo = {},
                onAbrirEjercicio = {},
                onAbrirCategoria = {},
                onReintentar = {},
            )
        }
    }
}
