package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.navigation.DestinoPrincipal
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Barra de navegación inferior. El ítem seleccionado se resalta con fondo verde lima. */
@Composable
fun VelokBottomBar(
    destinos: List<DestinoPrincipal>,
    seleccionado: DestinoPrincipal?,
    onSeleccionar: (DestinoPrincipal) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = VelokTheme.formasVelok.barraInferior,
        color = VelokTheme.colores.superficie,
        shadowElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .height(68.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinos.forEach { destino ->
                ItemBarraInferior(
                    destino = destino,
                    seleccionado = destino == seleccionado,
                    onClick = { onSeleccionar(destino) },
                )
            }
        }
    }
}

@Composable
private fun ItemBarraInferior(
    destino: DestinoPrincipal,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val colores = VelokTheme.colores
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(VelokTheme.formas.small)
            .background(if (seleccionado) colores.acentoSuave else Color.Transparent)
            .selectable(
                selected = seleccionado,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = null,
                indication = ripple(color = colores.acento),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(destino.icono),
            contentDescription = stringResource(destino.etiqueta),
            tint = if (seleccionado) colores.acentoTexto else colores.iconoInactivo,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Preview
@Composable
private fun VelokBottomBarPreview() {
    VelokTheme {
        VelokBottomBar(
            destinos = DestinoPrincipal.entries,
            seleccionado = DestinoPrincipal.INICIO,
            onSeleccionar = {},
        )
    }
}
