package com.velok.app.presentation.categoria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.presentation.components.EjercicioFila
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoError
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.TituloPantalla
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.nombre
import com.velok.app.presentation.util.resumenSeries
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.sin_ejercicios

/** Ejercicios de una categoría (se abre desde la pestaña "Entrenamiento"). */
@Composable
fun CategoriaScreen(
    categoria: TipoCategoria,
    onAbrirEjercicio: (String) -> Unit,
    viewModel: CategoriaViewModel = koinViewModel(key = categoria.name) { parametersOf(categoria) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    CategoriaContenido(estado, onAbrirEjercicio, onReintentar = viewModel::cargar)
}

@Composable
fun CategoriaContenido(
    estado: CategoriaUiState,
    onAbrirEjercicio: (String) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TituloPantalla(stringResource(estado.categoria.nombre))
        when {
            estado.cargando -> EstadoCargando()
            estado.error -> EstadoError(onReintentar)
            estado.ejercicios.isEmpty() -> EstadoVacio(stringResource(Res.string.sin_ejercicios))
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(estado.ejercicios, key = { it.id }) { ejercicio ->
                    EjercicioFila(
                        nombre = ejercicio.nombre,
                        detalle = ejercicio.resumenSeries(),
                        imagenUrl = ejercicio.imagenUrl,
                        onClick = { onAbrirEjercicio(ejercicio.id) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoriaContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            CategoriaContenido(
                estado = CategoriaUiState(
                    categoria = TipoCategoria.PIERNA,
                    cargando = false,
                    ejercicios = listOf(
                        Ejercicio("1", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA, Musculo.CUADRICEPS, emptyList(), ""),
                    ),
                ),
                onAbrirEjercicio = {},
                onReintentar = {},
            )
        }
    }
}
