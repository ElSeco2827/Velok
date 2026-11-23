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
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.presentation.components.TarjetaActividadSalud
import com.velok.app.presentation.components.TarjetaResumen
import com.velok.app.presentation.salud.AccionesSalud
import com.velok.app.presentation.salud.EstadoTarjetaSalud
import com.velok.app.presentation.salud.rememberAccionesSalud
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.resumen_info_general

/** "Pantalla progreso" (pestaña Actividad). */
@Composable
fun ProgresoScreen(viewModel: ProgresoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    // Permisos y tienda dependen de la plataforma (Health Connect / HealthKit).
    val accionesSalud = rememberAccionesSalud(recargar = viewModel::cargarSalud)
    ProgresoContenido(estado, accionesSalud, viewModel::cambiarMetrica, viewModel::cambiarRango)
}

@Composable
fun ProgresoContenido(
    estado: ProgresoUiState,
    accionesSalud: AccionesSalud,
    onMetrica: (MetricaProgreso) -> Unit,
    onRango: (RangoTiempo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        TarjetaActividadSalud(estado = estado.salud, acciones = accionesSalud)
        Spacer(Modifier.height(24.dp))
        TarjetaResumen(
            metrica = estado.metrica,
            rango = estado.rango,
            valores = estado.progreso,
            explicacion = stringResource(Res.string.resumen_info_general),
            onMetrica = onMetrica,
            onRango = onRango,
            unidad = estado.unidad,
        )
    }
}

@Preview
@Composable
private fun ProgresoContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            ProgresoContenido(
                estado = ProgresoUiState(
                    salud = EstadoTarjetaSalud(cargando = false, actividad = ActividadSalud.SinPermiso),
                    progreso = listOf(2000.0, 2600.0, 2400.0, 3100.0),
                ),
                accionesSalud = AccionesSalud(),
                onMetrica = {},
                onRango = {},
            )
        }
    }
}
