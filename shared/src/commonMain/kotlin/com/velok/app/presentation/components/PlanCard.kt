package com.velok.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatoLargo
import com.velok.app.presentation.util.nombreVisible
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.plan_minutos
import velok.shared.generated.resources.plan_series

/** Tarjeta de plan: imagen con etiqueta de categoría, nombre, fecha, series y duración estimada. */
@Composable
fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = VelokTheme.formas.small,
        color = colores.superficie,
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(122.dp)) {
                AsyncImage(
                    model = plan.imagenUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(colores.superficieOscura),
                    error = ColorPainter(colores.superficieOscura),
                    modifier = Modifier.fillMaxSize(),
                )
                EtiquetaCategoria(plan.categoria, Modifier.padding(8.dp))
            }
            Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Text(
                    text = plan.nombreVisible(),
                    style = VelokTheme.tipografia.titleMedium,
                    color = colores.textoPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = plan.fecha.formatoLargo(),
                    style = VelokTheme.tipografia.bodySmall,
                    color = colores.textoSecundario,
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Text(
                        text = stringResource(Res.string.plan_series, plan.totalSeries),
                        style = VelokTheme.tipografia.bodySmall,
                        color = colores.textoPrincipal,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(Res.string.plan_minutos, plan.duracionEstimadaMinutos),
                        style = VelokTheme.tipografia.bodySmall,
                        color = colores.textoPrincipal,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlanCardPreview() {
    VelokTheme {
        PlanCard(
            plan = Plan("plan", "Set del Lunes", TipoCategoria.PECHO, "", LocalDate(2026, 10, 12), emptyList()),
            onClick = {},
            modifier = Modifier.width(172.dp),
        )
    }
}
