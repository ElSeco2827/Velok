package com.velok.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme

/** Mini gráfica de área rellena en verde lima. Puramente decorativa. */
@Composable
fun MiniGraficaArea(
    valores: List<Float>,
    modifier: Modifier = Modifier,
) {
    val color = VelokTheme.colores.acento
    Canvas(modifier) {
        if (valores.size < 2) return@Canvas
        val minimo = valores.min()
        val rango = (valores.max() - minimo).takeIf { it > 0f } ?: 1f
        val paso = size.width / (valores.size - 1)
        // Deja un margen inferior para que el punto más bajo siga viéndose como área.
        val alturaUtil = size.height * 0.85f

        val area = Path().apply {
            moveTo(0f, size.height)
            valores.forEachIndexed { i, valor ->
                val y = size.height - ((valor - minimo) / rango) * alturaUtil - size.height * 0.15f
                lineTo(i * paso, y)
            }
            lineTo(size.width, size.height)
            close()
        }
        drawPath(area, color)
    }
}

@Preview
@Composable
private fun MiniGraficaAreaPreview() {
    VelokTheme {
        MiniGraficaArea(listOf(2f, 3f, 2.5f, 5f, 4f, 7f, 6f, 9f), Modifier.size(48.dp, 18.dp))
    }
}
