package com.velok.app.presentation.planes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.components.BotonFlotante
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.PlanCard
import com.velok.app.presentation.components.TituloPantalla
import com.velok.app.presentation.theme.VelokTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.crear_plan
import velok.shared.generated.resources.ic_mas
import velok.shared.generated.resources.lista_planes
import velok.shared.generated.resources.sin_planes

/** "Lista de planes" (pestaña de calendario): cuadrícula de planes y botón para crear uno. */
@Composable
fun PlanesScreen(
    onAbrirPlan: (String) -> Unit,
    onEditarPlan: (String) -> Unit,
    viewModel: PlanesViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    PlanesContenido(
        estado = estado,
        onAbrirPlan = onAbrirPlan,
        onCrearPlan = { viewModel.crear(onCreado = onEditarPlan) },
    )
}

@Composable
fun PlanesContenido(
    estado: PlanesUiState,
    onAbrirPlan: (String) -> Unit,
    onCrearPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TituloPantalla(stringResource(Res.string.lista_planes))
            when {
                estado.cargando -> EstadoCargando()
                estado.planes.isEmpty() -> EstadoVacio(stringResource(Res.string.sin_planes))
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    // Espacio abajo para que el botón flotante no tape la última fila.
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(estado.planes, key = { it.id }) { plan ->
                        PlanCard(plan = plan, onClick = { onAbrirPlan(plan.id) })
                    }
                }
            }
        }
        BotonFlotante(
            icono = Res.drawable.ic_mas,
            descripcion = stringResource(Res.string.crear_plan),
            onClick = onCrearPlan,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun PlanesContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            PlanesContenido(
                estado = PlanesUiState(
                    cargando = false,
                    planes = listOf(
                        Plan("1", "Set del Lunes", TipoCategoria.PECHO, "", LocalDate(2026, 10, 12), emptyList()),
                        Plan("2", "Set del Martes", TipoCategoria.BRAZOS, "", LocalDate(2026, 10, 13), emptyList()),
                        Plan("3", "Set del Miércoles", TipoCategoria.PIERNA, "", LocalDate(2026, 10, 16), emptyList()),
                    ),
                ),
                onAbrirPlan = {},
                onCrearPlan = {},
            )
        }
    }
}
