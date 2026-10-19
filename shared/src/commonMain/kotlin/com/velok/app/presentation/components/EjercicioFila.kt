package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ic_play

/** Fila de ejercicio: miniatura con ícono de reproducir, nombre y "12 × 4 series". */
@Composable
fun EjercicioFila(
    nombre: String,
    detalle: String,
    imagenUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(76.dp),
        shape = VelokTheme.formas.extraSmall,
        color = colores.superficie,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(116.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = imagenUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(colores.superficieOscura),
                    error = ColorPainter(colores.superficieOscura),
                    modifier = Modifier.fillMaxSize(),
                )
                Box(Modifier.fillMaxSize().background(colores.sombraTexto.copy(alpha = 0.15f)))
                Icon(
                    painter = painterResource(Res.drawable.ic_play),
                    contentDescription = null,
                    tint = colores.sobreImagen,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = nombre,
                    style = VelokTheme.tipografia.titleSmall,
                    color = colores.textoPrincipal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = detalle,
                    style = VelokTheme.tipografia.bodySmall,
                    color = colores.textoSecundario,
                )
            }
        }
    }
}

@Preview
@Composable
private fun EjercicioFilaPreview() {
    VelokTheme {
        EjercicioFila(
            nombre = "Press de banca plano (Barra)",
            detalle = "12 × 4 series",
            imagenUrl = "",
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
