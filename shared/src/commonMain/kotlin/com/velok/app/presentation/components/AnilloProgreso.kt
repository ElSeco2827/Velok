package com.velok.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.progreso_diario
import kotlin.math.roundToInt

/** Anillo de progreso (dona): pista negra y avance en verde lima, empezando arriba. */
@Composable
fun AnilloProgreso(
    progreso: Float,
    modifier: Modifier = Modifier,
    grosor: Dp = 12.dp,
) {
    val colores = VelokTheme.colores
    val animado by animateFloatAsState(
        targetValue = progreso.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
    )
    val descripcion = stringResource(Res.string.progreso_diario, (progreso * 100).roundToInt())
    Canvas(modifier.semantics { contentDescription = descripcion }) {
        val trazo = grosor.toPx()
        val diametro = size.minDimension - trazo
        val esquina = Offset((size.width - diametro) / 2, (size.height - diametro) / 2)
        val area = Size(diametro, diametro)
        drawArc(colores.encabezado, 0f, 360f, false, esquina, area, style = Stroke(trazo))
        drawArc(colores.acento, -90f, 360f * animado, false, esquina, area, style = Stroke(trazo))
    }
}

@Preview
@Composable
private fun AnilloProgresoPreview() {
    VelokTheme {
        AnilloProgreso(progreso = 0.7f, modifier = Modifier.size(64.dp))
    }
}
