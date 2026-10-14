package com.velok.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

internal val VelokShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Formas específicas de Velok que no encajan en la escala de Material. */
@Immutable
data class VelokFormas(
    val encabezado: RoundedCornerShape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
    val barraInferior: RoundedCornerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
)

internal val LocalVelokFormas = staticCompositionLocalOf { VelokFormas() }
