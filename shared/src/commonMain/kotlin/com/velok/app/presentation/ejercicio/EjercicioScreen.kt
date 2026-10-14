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
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.components.CategoriaCard
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoError
import com.velok.app.presentation.components.PlaceholderScreen
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.tab_ejercicio
import velok.shared.generated.resources.tab_entrenamiento

@Composable
fun EjercicioScreen(viewModel: EjercicioViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    EjercicioContenido(
        estado = estado,
        onSeleccionarPestana = viewModel::seleccionarPestana,
        onReintentar = viewModel::cargar,
    )
}

@Composable
fun EjercicioContenido(
    estado: EjercicioUiState,
    onSeleccionarPestana: (PestanaEjercicio) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        PestanasEjercicio(seleccionada = estado.pestana, onSeleccionar = onSeleccionarPestana)
        when (estado.pestana) {
            PestanaEjercicio.EJERCICIO -> when {
                estado.cargando -> EstadoCargando()
                estado.error -> EstadoError(onReintentar)
                else -> CuadriculaCategorias(estado.categorias)
            }
            PestanaEjercicio.ENTRENAMIENTO ->
                PlaceholderScreen(titulo = stringResource(Res.string.tab_entrenamiento))
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    style = VelokTheme.tipografia.labelLarge,
                    color = if (activa) colores.acentoTexto else colores.textoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (activa) colores.acento else Color.Transparent),
                )
            }
        }
    }
}

// Proporción ancho/alto por posición para el efecto escalonado del mockup.
private val proporciones = listOf(0.87f, 0.68f, 0.66f, 0.87f)

@Composable
private fun CuadriculaCategorias(categorias: List<CategoriaEjercicio>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(categorias, key = { _, categoria -> categoria.id }) { indice, categoria ->
            CategoriaCard(
                categoria = categoria,
                onClick = {},
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
        Surface(color = VelokTheme.colores.superficie) {
            EjercicioContenido(
                estado = EjercicioUiState(
                    cargando = false,
                    categorias = TipoCategoria.entries.map { CategoriaEjercicio(it.name, it, "") },
                ),
                onSeleccionarPestana = {},
                onReintentar = {},
            )
        }
    }
}
