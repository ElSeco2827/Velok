package com.velok.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.app_name
import velok.shared.generated.resources.atras
import velok.shared.generated.resources.ic_atras
import velok.shared.generated.resources.ic_notificacion
import velok.shared.generated.resources.logo_velok_encabezado
import velok.shared.generated.resources.notificaciones
import velok.shared.generated.resources.notificaciones_pendientes

// Tamaño del logo en el diseño (Header.png se exportó a 2x: 268x51 px).
private val ANCHO_LOGO = 134.dp
private val ALTO_LOGO = 25.5.dp

/**
 * Encabezado oscuro con el logo de Velok y la campana de notificaciones. Se extiende detrás de
 * la barra de estado. En pantallas de detalle muestra una flecha para volver (iOS no tiene botón
 * de "atrás" del sistema).
 */
@Composable
fun VelokHeader(
    tieneNotificaciones: Boolean,
    onNotificacionesClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAtras: (() -> Unit)? = null,
) {
    val colores = VelokTheme.colores
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colores.encabezado)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(64.dp)
            .padding(start = if (onAtras == null) 20.dp else 4.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onAtras != null) {
            IconButton(onClick = onAtras) {
                Icon(
                    painter = painterResource(Res.drawable.ic_atras),
                    contentDescription = stringResource(Res.string.atras),
                    tint = colores.sobreEncabezado,
                )
            }
        }
        Image(
            painter = painterResource(Res.drawable.logo_velok_encabezado),
            contentDescription = stringResource(Res.string.app_name),
            modifier = Modifier.width(ANCHO_LOGO).height(ALTO_LOGO),
        )
        Spacer(Modifier.weight(1f))

        val descripcion = if (tieneNotificaciones) {
            stringResource(Res.string.notificaciones_pendientes)
        } else {
            stringResource(Res.string.notificaciones)
        }
        IconButton(
            onClick = onNotificacionesClick,
            modifier = Modifier.semantics { contentDescription = descripcion },
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

@Preview
@Composable
private fun VelokHeaderPreview() {
    VelokTheme {
        VelokHeader(tieneNotificaciones = true, onNotificacionesClick = {})
    }
}

@Preview
@Composable
private fun VelokHeaderDetallePreview() {
    VelokTheme {
        VelokHeader(tieneNotificaciones = false, onNotificacionesClick = {}, onAtras = {})
    }
}
