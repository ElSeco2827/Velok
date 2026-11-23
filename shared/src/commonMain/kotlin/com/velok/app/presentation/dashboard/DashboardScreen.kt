package com.velok.app.presentation.dashboard

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
import androidx.compose.foundation.layout.width
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
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.components.PlanCard
import com.velok.app.presentation.components.TarjetaActividadSalud
import com.velok.app.presentation.salud.AccionesSalud
import com.velok.app.presentation.salud.EstadoTarjetaSalud
import com.velok.app.presentation.salud.rememberAccionesSalud
import com.velok.app.presentation.theme.VelokTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.planes
import velok.shared.generated.resources.ver_todos

@Composable
fun DashboardScreen(
    onVerTodosPlanes: () -> Unit,
    onAbrirPlan: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    // Permisos y tienda dependen de la plataforma (Health Connect / HealthKit).
    val accionesSalud = rememberAccionesSalud(recargar = viewModel::cargarSalud)
    DashboardContenido(
        estado = estado,
        accionesSalud = accionesSalud,
        onVerTodosPlanes = onVerTodosPlanes,
        onAbrirPlan = onAbrirPlan,
    )
}

@Composable
fun DashboardContenido(
    estado: DashboardUiState,
    accionesSalud: AccionesSalud,
    onVerTodosPlanes: () -> Unit,
    onAbrirPlan: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
    ) {
        TarjetaActividadSalud(
            estado = estado.salud,
            acciones = accionesSalud,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(32.dp))
        SeccionPlanes(planes = estado.planes, onVerTodos = onVerTodosPlanes, onAbrirPlan = onAbrirPlan)
    }
}

@Composable
private fun SeccionPlanes(
    planes: List<Plan>,
    onVerTodos: () -> Unit,
    onAbrirPlan: (String) -> Unit,
) {
    val colores = VelokTheme.colores
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.planes),
            style = VelokTheme.tipografia.titleMedium,
            color = colores.sobreFondo,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        Text(
            text = stringResource(Res.string.ver_todos),
            style = VelokTheme.tipografia.labelLarge,
            color = colores.acento,
            modifier = Modifier
                .clickable(role = Role.Button, onClick = onVerTodos)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        )
    }
    Spacer(Modifier.height(12.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(planes, key = { it.id }) { plan ->
            PlanCard(plan = plan, onClick = { onAbrirPlan(plan.id) }, modifier = Modifier.width(172.dp))
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
                    salud = EstadoTarjetaSalud(
                        cargando = false,
                        actividad = ActividadSalud.Datos(
                            ActividadDiaria(LocalDate(2026, 10, 12), 0.7f, pasos = 5_600),
                            listOf(
                                Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f)),
                                Metrica(TipoMetrica.PASOS, 5_600.0, listOf(1f, 2f, 4f, 3f, 5f, 8f)),
                                Metrica(TipoMetrica.DISTANCIA, 4.1, listOf(3f, 2f, 4f, 3.5f, 6f, 8f)),
                                Metrica(TipoMetrica.RITMO_CARDIACO, 90.0, listOf(5f, 7f, 6f, 8f, 6f, 7f)),
                            ),
                        ),
                    ),
                    planes = listOf(
                        Plan("plan-lunes", "Set del Lunes", TipoCategoria.PECHO, "", LocalDate(2026, 10, 12), emptyList()),
                        Plan("plan-martes", "Set del Martes", TipoCategoria.ESPALDA, "", LocalDate(2026, 10, 13), emptyList()),
                    ),
                ),
                accionesSalud = AccionesSalud(),
                onVerTodosPlanes = {},
                onAbrirPlan = {},
            )
        }
    }
}
