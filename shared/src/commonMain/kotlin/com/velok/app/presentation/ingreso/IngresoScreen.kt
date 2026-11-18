package com.velok.app.presentation.ingreso

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.ErroresIngreso
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasCuenta
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.CampoCuenta
import com.velok.app.presentation.components.EnlaceTexto
import com.velok.app.presentation.components.PlantillaCuenta
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.mensaje
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.contrasena
import velok.shared.generated.resources.contrasena_placeholder
import velok.shared.generated.resources.correo_electronico
import velok.shared.generated.resources.correo_placeholder
import velok.shared.generated.resources.iniciar_sesion
import velok.shared.generated.resources.iniciar_sesion_google
import velok.shared.generated.resources.olvidaste_contrasena

/** "Inicio de sesión" y su estado con errores. */
@Composable
fun IngresoScreen(
    onRecuperar: () -> Unit,
    onListo: (DestinoInicial) -> Unit,
    viewModel: IngresoViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    IngresoContenido(
        estado = estado,
        onCorreo = viewModel::cambiarCorreo,
        onContrasena = viewModel::cambiarContrasena,
        onIngresar = { viewModel.ingresar(onListo) },
        onGoogle = { viewModel.ingresarGoogle(onListo) },
        onRecuperar = onRecuperar,
    )
}

@Composable
fun IngresoContenido(
    estado: IngresoUiState,
    onCorreo: (String) -> Unit,
    onContrasena: (String) -> Unit,
    onIngresar: () -> Unit,
    onGoogle: () -> Unit,
    onRecuperar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlantillaCuenta(modifier) {
        Spacer(Modifier.height(150.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.correo_electronico),
            valor = estado.correo,
            onCambio = onCorreo,
            placeholder = stringResource(Res.string.correo_placeholder),
            error = estado.errores.correo?.mensaje(),
            teclado = KeyboardType.Email,
        )
        Spacer(Modifier.height(40.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.contrasena),
            valor = estado.contrasena,
            onCambio = onContrasena,
            placeholder = stringResource(Res.string.contrasena_placeholder, ReglasCuenta.CONTRASENA_MINIMA),
            error = estado.errores.contrasena?.mensaje(),
            esContrasena = true,
            accionTeclado = ImeAction.Done,
        )
        Spacer(Modifier.height(80.dp))
        estado.errorCuenta?.let {
            Text(
                it.mensaje(),
                color = VelokTheme.colores.alerta,
                style = VelokTheme.tipografia.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
        EnlaceTexto(
            stringResource(Res.string.olvidaste_contrasena),
            onClick = onRecuperar,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))
        BotonVelok(
            texto = stringResource(Res.string.iniciar_sesion),
            onClick = onIngresar,
            cargando = estado.cargando,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(64.dp))
        BotonVelok(
            texto = stringResource(Res.string.iniciar_sesion_google),
            onClick = onGoogle,
            cargando = estado.cargandoGoogle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Preview
@Composable
private fun IngresoContenidoPreview() {
    VelokTheme {
        IngresoContenido(IngresoUiState(), {}, {}, {}, {}, {})
    }
}

@Preview
@Composable
private fun IngresoConErrorPreview() {
    VelokTheme {
        IngresoContenido(
            IngresoUiState(
                correo = "ejemplo",
                errores = ErroresIngreso(ErrorCampo.CORREO_INVALIDO, ErrorCampo.CONTRASENA_CORTA),
                errorCuenta = ErrorCuenta.CREDENCIALES_INCORRECTAS,
            ),
            {}, {}, {}, {}, {},
        )
    }
}
