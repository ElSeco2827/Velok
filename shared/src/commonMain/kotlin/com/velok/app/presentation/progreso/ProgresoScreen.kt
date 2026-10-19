package com.velok.app.presentation.progreso

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoError
import com.velok.app.presentation.components.TarjetaActividadDiaria
import com.velok.app.presentation.components.TarjetaResumen
import com.velok.app.presentation.theme.VelokTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.resumen_info_general

/** "Pantalla progreso" (pestaña Actividad). */
@Composable
fun ProgresoScreen(viewModel: ProgresoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    ProgresoContenido(estado, viewModel::cargar, viewModel::cambiarMetrica, viewModel::cambiarRango)
}

@Composable
fun ProgresoContenido(
    estado: ProgresoUiState,
    onReintentar: () -> Unit,
    onMetrica: (MetricaProgreso) -> Unit,
    onRango: (RangoTiempo) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        estado.cargando -> EstadoCargando(modifier)
        estado.error || estado.actividad == null -> EstadoError(onReintentar, modifier)
        else -> Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            TarjetaActividadDiaria(actividad = estado.actividad, metricas = estado.metricas)
            Spacer(Modifier.height(24.dp))
            TarjetaResumen(
                metrica = estado.metrica,
                rango = estado.rango,
                valores = estado.progreso,
                explicacion = stringResource(Res.string.resumen_info_general),
                onMetrica = onMetrica,
                onRango = onRango,
            )
        }
    }
}

@Preview
@Composable
private fun ProgresoContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            ProgresoContenido(
                estado = ProgresoUiState(
                    cargando = false,
                    actividad = ActividadDiaria(LocalDate(2026, 10, 12), 0.7f),
                    metricas = listOf(
                        Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 5f, 7f)),
                        Metrica(TipoMetrica.PASOS, 2032.0, listOf(1f, 4f, 3f, 8f)),
                    ),
                    progreso = listOf(2000.0, 2600.0, 2400.0, 3100.0),
                ),
                onReintentar = {},
                onMetrica = {},
                onRango = {},
            )
        }
    }
}
