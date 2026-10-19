package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.icono
import com.velok.app.presentation.util.nombre
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Etiqueta sobre la foto de un plan ("Pecho" con su ícono). */
@Composable
fun EtiquetaCategoria(categoria: TipoCategoria, modifier: Modifier = Modifier) {
    val colores = VelokTheme.colores
    Row(
        modifier = modifier
            .clip(VelokTheme.formas.extraSmall)
            .background(colores.chipActivo.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(categoria.icono),
            contentDescription = null,
            tint = colores.acento,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = stringResource(categoria.nombre),
            style = VelokTheme.tipografia.labelSmall,
            color = colores.acento,
        )
    }
}

/** Chip de filtro por equipo (Barra, Pesa, Tapete, Máquina). */
@Composable
fun ChipEquipo(
    equipo: TipoEquipo,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val contenido = if (seleccionado) colores.acento else colores.textoSecundario
    Surface(
        selected = seleccionado,
        onClick = onClick,
        modifier = modifier,
        shape = VelokTheme.formas.extraSmall,
        color = if (seleccionado) colores.chipActivo else colores.chipInactivo,
        border = if (seleccionado) BorderStroke(1.dp, colores.acento.copy(alpha = 0.4f)) else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            Icon(
                painter = painterResource(equipo.icono),
                contentDescription = null,
                tint = contenido,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(equipo.nombre),
                style = VelokTheme.tipografia.labelSmall,
                color = contenido,
                maxLines = 1,
            )
        }
    }
}

/** Chip oscuro de acción ("Descanso: 1min 0s", "Nota"). */
@Composable
fun ChipOscuro(
    icono: DrawableResource,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
) {
    val colores = VelokTheme.colores
    val contenido = if (destacado) colores.acento else colores.sobreFondo
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = VelokTheme.formas.small,
        color = colores.superficieOscura,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(painterResource(icono), contentDescription = null, tint = contenido, modifier = Modifier.size(18.dp))
            Text(texto, style = VelokTheme.tipografia.bodyMedium, color = contenido)
        }
    }
}

/** Botón cuadrado verde suave de los diseños ("+" en planes, "▶" en detalle de plan). */
@Composable
fun BotonFlotante(
    icono: DrawableResource,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier.size(52.dp),
        shape = VelokTheme.formas.small,
        color = colores.acentoSuave,
        border = BorderStroke(1.dp, colores.superficie),
        shadowElevation = 6.dp,
    ) {
        Icon(
            painter = painterResource(icono),
            contentDescription = descripcion,
            tint = colores.acentoTexto,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Preview
@Composable
private fun ChipsPreview() {
    VelokTheme {
        Row(
            Modifier.background(VelokTheme.colores.fondo).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipEquipo(TipoEquipo.BARRA, seleccionado = true, onClick = {})
            ChipEquipo(TipoEquipo.PESA, seleccionado = false, onClick = {})
            EtiquetaCategoria(TipoCategoria.PECHO)
        }
    }
}
