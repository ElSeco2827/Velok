package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.etiqueta
import com.velok.app.presentation.util.icono
import com.velok.app.presentation.util.valorFormateado
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Tarjeta de una métrica: ícono, etiqueta, valor y mini gráfica.
 *
 * @param alterna usa el fondo crema en lugar del blanco con borde (como en el mockup).
 */
@Composable
fun MetricaCard(
    metrica: Metrica,
    modifier: Modifier = Modifier,
    alterna: Boolean = false,
) {
    val colores = VelokTheme.colores
    val etiqueta = stringResource(metrica.tipo.etiqueta)
    val valor = metrica.valorFormateado()

    Surface(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$etiqueta: $valor" },
        shape = VelokTheme.formas.medium,
        color = if (alterna) colores.superficieAlterna else colores.superficie,
        border = if (alterna) null else BorderStroke(1.dp, colores.borde),
    ) {
        Column(
            modifier = Modifier
                .clearAndSetSemantics { }
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(VelokTheme.formas.small)
                        .background(if (alterna) colores.superficie else colores.superficieAlterna),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(metrica.tipo.icono),
                        contentDescription = null,
                        tint = colores.acentoTexto,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = etiqueta,
                    style = VelokTheme.tipografia.labelSmall,
                    color = colores.textoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = valor,
                style = VelokTheme.tipografia.headlineSmall,
                color = colores.textoPrincipal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            MiniGraficaArea(
                valores = metrica.historial,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 48.dp, height = 18.dp),
            )
        }
    }
}

@Preview
@Composable
private fun MetricaCardPreview() {
    VelokTheme {
        Row(Modifier.background(VelokTheme.colores.superficie).padding(16.dp)) {
            MetricaCard(
                metrica = Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f)),
                modifier = Modifier.width(150.dp),
            )
            Spacer(Modifier.width(12.dp))
            MetricaCard(
                metrica = Metrica(TipoMetrica.PASOS, 2032.0, listOf(1f, 2f, 4f, 3f, 5f, 8f)),
                modifier = Modifier.width(150.dp),
                alterna = true,
            )
        }
    }
}
