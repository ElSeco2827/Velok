package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme

/**
 * Estructura común de las pantallas de cuenta (inicio de sesión, registro, recuperación, textos
 * legales): fondo oscuro, logo horizontal centrado arriba y contenido desplazable que se acomoda
 * al teclado.
 */
@Composable
fun PlantillaCuenta(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VelokTheme.colores.fondo)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(60.dp))
        LogoHorizontal(Modifier.align(Alignment.CenterHorizontally).width(266.dp))
        contenido()
        Spacer(Modifier.height(24.dp))
    }
}
