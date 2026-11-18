package com.velok.app.presentation.bienvenida

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.LogoAguila
import com.velok.app.presentation.components.LogoTexto
import com.velok.app.presentation.components.TextoConEnlaces
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.mensaje
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.inicia_sesion_enlace
import velok.shared.generated.resources.registrate_correo
import velok.shared.generated.resources.registrate_google
import velok.shared.generated.resources.tienes_cuenta

/** "Selección de inicio": registro con Google o con correo, o ir a iniciar sesión. */
@Composable
fun BienvenidaScreen(
    onRegistroCorreo: () -> Unit,
    onIniciarSesion: () -> Unit,
    onListo: (DestinoInicial) -> Unit,
    viewModel: BienvenidaViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    BienvenidaContenido(
        estado = estado,
        onGoogle = { viewModel.continuarConGoogle(onListo) },
        onRegistroCorreo = onRegistroCorreo,
        onIniciarSesion = onIniciarSesion,
    )
}

@Composable
fun BienvenidaContenido(
    estado: BienvenidaUiState,
    onGoogle: () -> Unit,
    onRegistroCorreo: () -> Unit,
    onIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .systemBarsPadding()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(80.dp))
        // En el diseño el águila mide 303 pt de los 313 que quedan entre los márgenes de 40.
        LogoAguila(Modifier.fillMaxWidth(0.97f), decorativo = true)
        Spacer(Modifier.height(36.dp))
        LogoTexto(Modifier.fillMaxWidth(0.42f))
        Spacer(Modifier.weight(1f))
        BotonVelok(
            texto = stringResource(Res.string.registrate_google),
            onClick = onGoogle,
            cargando = estado.cargandoGoogle,
            modifier = Modifier.fillMaxWidth(),
        )
        estado.error?.let {
            Text(it.mensaje(), color = colores.alerta, style = VelokTheme.tipografia.bodySmall,
                textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(48.dp))
        BotonVelok(
            texto = stringResource(Res.string.registrate_correo),
            onClick = onRegistroCorreo,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(48.dp))
        TextoConEnlaces(
            listOf(
                stringResource(Res.string.tienes_cuenta) to null,
                stringResource(Res.string.inicia_sesion_enlace) to onIniciarSesion,
            ),
        )
        Spacer(Modifier.height(48.dp))
    }
}

@Preview
@Composable
private fun BienvenidaContenidoPreview() {
    VelokTheme { BienvenidaContenido(BienvenidaUiState(), {}, {}, {}) }
}
