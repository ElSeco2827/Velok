package com.velok.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// La app es oscura, pero los componentes de Material (diálogos, menús, hojas, campos) se muestran
// como tarjetas claras, igual que las tarjetas del diseño. Por eso se parte de un esquema claro
// y solo el fondo general es oscuro.
private val VelokColorScheme = lightColorScheme(
    primary = VerdeLima,
    onPrimary = Negro,
    primaryContainer = VerdeLimaSuave,
    onPrimaryContainer = Negro,
    secondary = Negro,
    onSecondary = Blanco,
    background = Carbon,
    onBackground = Blanco,
    surface = Blanco,
    onSurface = Negro,
    surfaceVariant = GrisPizarraClaro,
    onSurfaceVariant = GrisTexto,
    surfaceContainer = Blanco,
    surfaceContainerLow = Blanco,
    surfaceContainerHigh = Blanco,
    outline = BordeSutil,
    outlineVariant = BordeSutil,
    error = RojoAlerta,
)

@Composable
fun VelokTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalVelokColores provides VelokColoresOscuros,
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
