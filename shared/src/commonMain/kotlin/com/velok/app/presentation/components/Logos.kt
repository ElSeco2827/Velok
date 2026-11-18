package com.velok.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.app_name
import velok.shared.generated.resources.logo_velok_aguila
import velok.shared.generated.resources.logo_velok_encabezado
import velok.shared.generated.resources.logo_velok_texto

// Proporciones de los recursos originales (docs/diseno).
private const val PROPORCION_AGUILA = 302f / 139f
private const val PROPORCION_TEXTO = 133f / 22f
private const val PROPORCION_HORIZONTAL = 268f / 51f

/** Águila de Velok en vector (docs/diseno/logo_velok.svg con los trazos rellenos). */
@Composable
fun LogoAguila(modifier: Modifier = Modifier, decorativo: Boolean = false) {
    Image(
        painter = painterResource(Res.drawable.logo_velok_aguila),
        contentDescription = if (decorativo) null else stringResource(Res.string.app_name),
        modifier = modifier.aspectRatio(PROPORCION_AGUILA),
    )
}

/** Letrero "VÉLOK" (recorte exacto de docs/diseno/Header.png). */
@Composable
fun LogoTexto(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo_velok_texto),
        contentDescription = stringResource(Res.string.app_name),
        modifier = modifier.aspectRatio(PROPORCION_TEXTO),
    )
}

/** Logo horizontal (águila + "VÉLOK") de las pantallas de cuenta: docs/diseno/Header.png. */
@Composable
fun LogoHorizontal(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo_velok_encabezado),
        contentDescription = stringResource(Res.string.app_name),
        modifier = modifier.aspectRatio(PROPORCION_HORIZONTAL),
    )
}

@Preview
@Composable
private fun LogosPreview() {
    VelokTheme {
        androidx.compose.foundation.layout.Column {
            LogoAguila(Modifier.width(200.dp))
            LogoTexto(Modifier.width(130.dp))
            LogoHorizontal(Modifier.height(50.dp))
        }
    }
}
