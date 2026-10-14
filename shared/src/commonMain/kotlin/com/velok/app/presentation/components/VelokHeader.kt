package com.velok.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.app_name
import velok.shared.generated.resources.ic_notificacion
import velok.shared.generated.resources.logo_velok
import velok.shared.generated.resources.notificaciones
import velok.shared.generated.resources.notificaciones_pendientes

// Proporciones medidas en el mockup, relativas al ancho del encabezado.
private const val ALTO_CONTENIDO = 0.206f
private const val ANCHO_LOGO = 0.752f
private const val MARGEN_IZQUIERDO_LOGO = 0.117f
private const val MARGEN_SUPERIOR_LOGO = 0.042f

/**
 * Encabezado negro con esquinas inferiores redondeadas, logo de Velok en verde lima
 * y campana de notificaciones. Se extiende detrás de la barra de estado.
 */
@Composable
fun VelokHeader(
    tieneNotificaciones: Boolean,
    onNotificacionesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(VelokTheme.formasVelok.encabezado)
            .background(colores.encabezado)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        val ancho = maxWidth
        Box(Modifier.fillMaxWidth().height(ancho * ALTO_CONTENIDO)) {
            // El logo es más alto que el encabezado: se dibuja completo y el clip lo corta abajo.
            Image(
                painter = painterResource(Res.drawable.logo_velok),
                contentDescription = stringResource(Res.string.app_name),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .offset(x = ancho * MARGEN_IZQUIERDO_LOGO, y = ancho * MARGEN_SUPERIOR_LOGO)
                    .requiredWidth(ancho * ANCHO_LOGO),
            )

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
}

@Preview
@Composable
private fun VelokHeaderPreview() {
    VelokTheme {
        VelokHeader(tieneNotificaciones = true, onNotificacionesClick = {})
    }
}
