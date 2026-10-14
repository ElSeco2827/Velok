package com.velok.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ic_notificacion
import velok.shared.generated.resources.notificaciones
import velok.shared.generated.resources.notificaciones_pendientes

/**
 * Encabezado negro con esquinas inferiores redondeadas, trazo decorativo verde lima
 * y campana de notificaciones. Se extiende detrás de la barra de estado.
 */
@Composable
fun VelokHeader(
    tieneNotificaciones: Boolean,
    onNotificacionesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(VelokTheme.formasVelok.encabezado)
            .background(colores.encabezado)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(76.dp),
    ) {
        TrazoDecorativo(Modifier.fillMaxSize())

        val descripcion = if (tieneNotificaciones) {
            stringResource(Res.string.notificaciones_pendientes)
        } else {
            stringResource(Res.string.notificaciones)
        }
        IconButton(
            onClick = onNotificacionesClick,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .semantics { contentDescription = descripcion },
        ) {
            Box {
                Icon(
                    painter = painterResource(Res.drawable.ic_notificacion),
                    contentDescription = null,
                    tint = colores.sobreEncabezado,
                    modifier = Modifier.size(24.dp),
                )
                if (tieneNotificaciones) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 1.dp, y = (-1).dp)
                            .size(9.dp)
                            .border(1.5.dp, colores.encabezado, CircleShape)
                            .clip(CircleShape)
                            .background(colores.alerta),
                    )
                }
            }
        }
    }
}

/** Líneas angulares en verde lima inspiradas en el mockup. */
@Composable
private fun TrazoDecorativo(modifier: Modifier = Modifier) {
    val color = VelokTheme.colores.acento
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        fun p(x: Float, y: Float) = Offset(w * x, h * y)
        val estilo = Stroke(width = 1.4.dp.toPx(), join = StrokeJoin.Round)

        val exterior = Path().apply {
            p(0.10f, 0.22f).let { moveTo(it.x, it.y) }
            listOf(p(0.70f, 0.34f), p(0.80f, 0.56f), p(0.92f, 1.10f)).forEach { lineTo(it.x, it.y) }
        }
        val interior = Path().apply {
            p(0.10f, 0.22f).let { moveTo(it.x, it.y) }
            listOf(p(0.28f, 0.62f), p(0.52f, 0.66f), p(0.46f, 1.10f)).forEach { lineTo(it.x, it.y) }
        }
        val chevron = Path().apply {
            p(0.22f, 0.80f).let { moveTo(it.x, it.y) }
            listOf(p(0.40f, 0.84f), p(0.34f, 1.10f)).forEach { lineTo(it.x, it.y) }
        }
        drawPath(exterior, color, style = estilo)
        drawPath(interior, color, style = estilo)
        drawPath(chevron, color, style = estilo)
    }
}

@Preview
@Composable
private fun VelokHeaderPreview() {
    VelokTheme {
        VelokHeader(tieneNotificaciones = true, onNotificacionesClick = {})
    }
}
