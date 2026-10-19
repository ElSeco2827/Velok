package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatoLargo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.actividad_diaria

/** Tarjeta "Actividad diaria": fecha, anillo de progreso y cuadrícula 2x2 de métricas. */
@Composable
fun TarjetaActividadDiaria(
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
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                AnilloProgreso(progreso = actividad.progreso, modifier = Modifier.size(82.dp), grosor = 16.dp)
            }
            Spacer(Modifier.height(16.dp))
            metricas.chunked(2).forEachIndexed { fila, par ->
                if (fila > 0) Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    par.forEach { metrica ->
                        MetricaCard(metrica = metrica, modifier = Modifier.weight(1f))
                    }
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun TarjetaActividadDiariaPreview() {
    VelokTheme {
        TarjetaActividadDiaria(
            actividad = ActividadDiaria(LocalDate(2026, 10, 12), 0.7f),
            metricas = listOf(
                Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f)),
                Metrica(TipoMetrica.PASOS, 2032.0, listOf(1f, 2f, 4f, 3f, 5f, 8f)),
                Metrica(TipoMetrica.DISTANCIA, 1.5, listOf(3f, 2f, 4f, 3.5f, 6f, 8f)),
                Metrica(TipoMetrica.RITMO_CARDIACO, 90.0, listOf(5f, 7f, 6f, 8f, 6f, 7f)),
            ),
        )
    }
}
