package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.icono
import com.velok.app.presentation.util.nombre
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Tarjeta de categoría: imagen de fondo, degradado verde lima abajo, ícono y nombre. */
@Composable
fun CategoriaCard(
    categoria: CategoriaEjercicio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = VelokTheme.formas.medium,
        color = colores.encabezado,
    ) {
        Box {
            AsyncImage(
                model = categoria.imagenUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(colores.encabezado),
                error = ColorPainter(colores.encabezado),
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1f to colores.acento.copy(alpha = 0.95f),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(26.dp)
                    .clip(VelokTheme.formas.extraSmall)
                    .background(colores.acento),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(categoria.tipo.icono),
                    contentDescription = null,
                    tint = colores.sobreAcento,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = stringResource(categoria.tipo.nombre),
                style = VelokTheme.tipografia.titleMedium.copy(
                    shadow = Shadow(colores.sombraTexto, Offset(0f, 1f), blurRadius = 6f),
                ),
                color = colores.sobreImagen,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CategoriaCardPreview() {
    VelokTheme {
        CategoriaCard(
            categoria = CategoriaEjercicio("pecho", TipoCategoria.PECHO, imagenUrl = ""),
            onClick = {},
            modifier = Modifier.size(160.dp, 190.dp),
        )
    }
}
