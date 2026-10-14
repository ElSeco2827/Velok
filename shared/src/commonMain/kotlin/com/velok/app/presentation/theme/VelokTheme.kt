package com.velok.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val VelokColorScheme = lightColorScheme(
    primary = VerdeLima,
    onPrimary = Negro,
    primaryContainer = VerdeLimaSuave,
    onPrimaryContainer = Negro,
    secondary = Negro,
    onSecondary = Blanco,
    background = Crema,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    surfaceVariant = CremaTarjeta,
    onSurfaceVariant = GrisTexto,
    outline = BordeSutil,
    outlineVariant = BordeSutil,
    error = RojoAlerta,
)

@Composable
fun VelokTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalVelokColores provides VelokColoresClaros,
        LocalVelokFormas provides VelokFormas(),
    ) {
        MaterialTheme(
            colorScheme = VelokColorScheme,
            typography = VelokTipografia,
            shapes = VelokShapes,
            content = content,
        )
    }
}

/** Acceso a los tokens del tema: `VelokTheme.colores.acento`, `VelokTheme.formas.medium`, etc. */
object VelokTheme {
    val colores: VelokColores
        @Composable @ReadOnlyComposable get() = LocalVelokColores.current

    val tipografia: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val formas: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes

    val formasVelok: VelokFormas
        @Composable @ReadOnlyComposable get() = LocalVelokFormas.current
}
