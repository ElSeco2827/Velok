package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.icono
import com.velok.app.presentation.util.nombre
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.plan_ejercicios

/** Tarjeta de plan para el carrusel horizontal del Dashboard. */
@Composable
fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier.width(150.dp),
        shape = VelokTheme.formas.medium,
        color = colores.superficie,
        border = BorderStroke(1.dp, colores.borde),
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(116.dp)) {
                AsyncImage(
                    model = plan.imagenUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(colores.encabezado),
                    error = ColorPainter(colores.encabezado),
                    modifier = Modifier.fillMaxSize(),
                )
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(plan.categoria.icono),
                        contentDescription = null,
                        tint = colores.acento,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(plan.categoria.nombre),
                        style = VelokTheme.tipografia.labelSmall,
                        color = colores.acento,
                    )
                }
            }
            Text(
                text = stringResource(Res.string.plan_ejercicios, plan.numeroEjercicios),
                style = VelokTheme.tipografia.bodySmall,
                color = colores.textoSecundario,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PlanCardPreview() {
    VelokTheme {
        PlanCard(
            plan = Plan("plan-pecho", TipoCategoria.PECHO, imagenUrl = "", numeroEjercicios = 8),
            onClick = {},
        )
    }
}
