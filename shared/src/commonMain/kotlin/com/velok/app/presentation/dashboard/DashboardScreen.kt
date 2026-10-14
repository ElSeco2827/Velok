package com.velok.app.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.components.AnilloProgreso
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoError
import com.velok.app.presentation.components.MetricaCard
import com.velok.app.presentation.components.PlanCard
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatoLargo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.actividad_diaria
import velok.shared.generated.resources.planes
import velok.shared.generated.resources.ver_todos

@Composable
fun DashboardScreen(
    onVerTodosPlanes: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    DashboardContenido(
        estado = estado,
        onReintentar = viewModel::cargar,
        onVerTodosPlanes = onVerTodosPlanes,
    )
}

@Composable
fun DashboardContenido(
    estado: DashboardUiState,
    onReintentar: () -> Unit,
    onVerTodosPlanes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        estado.cargando -> EstadoCargando(modifier)
        estado.error || estado.actividad == null -> EstadoError(onReintentar, modifier)
        else -> Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
        ) {
            TarjetaActividadDiaria(
                actividad = estado.actividad,
                metricas = estado.metricas,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(20.dp))
            SeccionPlanes(planes = estado.planes, onVerTodos = onVerTodosPlanes)
        }
    }
}

@Composable
private fun TarjetaActividadDiaria(
    actividad: ActividadDiaria,
    metricas: List<Metrica>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = VelokTheme.formas.large,
        color = colores.superficie,
        border = BorderStroke(1.dp, colores.borde),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.actividad_diaria),
                        style = VelokTheme.tipografia.titleLarge,
                        color = colores.acentoTexto,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = actividad.fecha.formatoLargo(),
                        style = VelokTheme.tipografia.bodySmall,
                        color = colores.textoSecundario,
                    )
                }
                AnilloProgreso(progreso = actividad.progreso, modifier = Modifier.size(64.dp))
            }
            Spacer(Modifier.height(14.dp))
            // Cuadrícula 2x2: columna izquierda blanca con borde, derecha crema (como en el mockup).
            metricas.chunked(2).forEachIndexed { fila, par ->
                if (fila > 0) Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    par.forEachIndexed { columna, metrica ->
                        MetricaCard(
                            metrica = metrica,
                            alterna = columna == 1,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SeccionPlanes(
    planes: List<Plan>,
    onVerTodos: () -> Unit,
) {
    val colores = VelokTheme.colores
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.planes),
            style = VelokTheme.tipografia.titleMedium,
            color = colores.textoPrincipal,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        Text(
            text = stringResource(Res.string.ver_todos),
            style = VelokTheme.tipografia.labelLarge,
            color = colores.acentoTexto,
            modifier = Modifier
                .clickable(role = Role.Button, onClick = onVerTodos)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        )
    }
    Spacer(Modifier.height(8.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(planes, key = { it.id }) { plan ->
            PlanCard(plan = plan, onClick = onVerTodos)
        }
    }
}

@Preview
@Composable
private fun DashboardContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            DashboardContenido(
                estado = DashboardUiState(
                    cargando = false,
                    actividad = ActividadDiaria(LocalDate(2026, 10, 12), 0.7f),
                    metricas = listOf(
                        Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f)),
                        Metrica(TipoMetrica.PASOS, 2032.0, listOf(1f, 2f, 4f, 3f, 5f, 8f)),
                        Metrica(TipoMetrica.DISTANCIA, 1.5, listOf(3f, 2f, 4f, 3.5f, 6f, 8f)),
                        Metrica(TipoMetrica.RITMO_CARDIACO, 90.0, listOf(5f, 7f, 6f, 8f, 6f, 7f)),
                    ),
                    planes = listOf(
                        Plan("plan-pecho", TipoCategoria.PECHO, "", 8),
                        Plan("plan-espalda", TipoCategoria.ESPALDA, "", 10),
                    ),
                ),
                onReintentar = {},
                onVerTodosPlanes = {},
            )
        }
    }
}
