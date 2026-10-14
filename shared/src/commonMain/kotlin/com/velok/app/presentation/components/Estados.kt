package com.velok.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.error_carga
import velok.shared.generated.resources.nav_perfil
import velok.shared.generated.resources.proximamente
import velok.shared.generated.resources.reintentar

@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = VelokTheme.colores.acentoTexto,
            trackColor = VelokTheme.colores.acentoSuave,
        )
    }
}

@Composable
fun EstadoError(onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(Res.string.error_carga),
            style = VelokTheme.tipografia.bodyMedium,
            color = VelokTheme.colores.textoPrincipal,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onReintentar,
            colors = ButtonDefaults.buttonColors(
                containerColor = VelokTheme.colores.acento,
                contentColor = VelokTheme.colores.sobreAcento,
            ),
        ) {
            Text(stringResource(Res.string.reintentar))
        }
    }
}

/** Pantalla provisional para secciones que aún no se implementan. */
@Composable
fun PlaceholderScreen(
    titulo: String,
    modifier: Modifier = Modifier,
    detalle: String? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = titulo,
            style = VelokTheme.tipografia.titleLarge,
            color = VelokTheme.colores.textoPrincipal,
        )
        Text(
            text = stringResource(Res.string.proximamente),
            style = VelokTheme.tipografia.bodyMedium,
            color = VelokTheme.colores.textoSecundario,
        )
        if (detalle != null) {
            Text(
                text = detalle,
                style = VelokTheme.tipografia.labelLarge,
                color = VelokTheme.colores.acentoTexto,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun PlaceholderScreenPreview() {
    VelokTheme {
        PlaceholderScreen(titulo = stringResource(Res.string.nav_perfil), detalle = "Ejecutando en Android 37")
    }
}
