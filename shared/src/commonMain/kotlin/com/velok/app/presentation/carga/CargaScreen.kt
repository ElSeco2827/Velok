package com.velok.app.presentation.carga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.presentation.components.LogoAguila
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.cargando

/** "Página inicial (Cargando)": águila centrada mientras se decide la primera pantalla. */
@Composable
fun CargaScreen(
    onDestino: (DestinoInicial) -> Unit,
    viewModel: CargaViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.destino) { estado.destino?.let(onDestino) }
    CargaContenido()
}

@Composable
fun CargaContenido(modifier: Modifier = Modifier) {
    val descripcion = stringResource(Res.string.cargando)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VelokTheme.colores.fondo)
            .semantics { contentDescription = descripcion },
        contentAlignment = Alignment.Center,
    ) {
        // En el diseño el águila ocupa 303 de 393 pt de ancho.
        LogoAguila(Modifier.fillMaxWidth(0.77f), decorativo = true)
    }
}

@Preview
@Composable
private fun CargaContenidoPreview() {
    VelokTheme { CargaContenido() }
}
