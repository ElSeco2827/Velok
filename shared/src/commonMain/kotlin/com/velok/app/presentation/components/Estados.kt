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
import velok.shared.generated.resources.reintentar
import velok.shared.generated.resources.sin_planes

@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = VelokTheme.colores.acento,
            trackColor = VelokTheme.colores.superficieOscura,
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
            color = VelokTheme.colores.sobreFondo,
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

/** Mensaje centrado para listas vacías o elementos que no existen. */
@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = mensaje,
            style = VelokTheme.tipografia.bodyMedium,
            color = VelokTheme.colores.sobreFondoSecundario,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun EstadoVacioPreview() {
    VelokTheme {
        EstadoVacio(stringResource(Res.string.sin_planes))
    }
}
