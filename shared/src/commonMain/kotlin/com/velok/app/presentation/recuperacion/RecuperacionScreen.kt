package com.velok.app.presentation.recuperacion

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.DestinoInicial
import com.velok.app.domain.validacion.ReglasCuenta
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.CampoCuenta
import com.velok.app.presentation.components.PlantillaCuenta
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.mensaje
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.confirmar_contrasena
import velok.shared.generated.resources.confirmar_placeholder
import velok.shared.generated.resources.continuar
import velok.shared.generated.resources.contrasena_nueva
import velok.shared.generated.resources.contrasena_placeholder
import velok.shared.generated.resources.correo_electronico
import velok.shared.generated.resources.correo_placeholder

/** "Recuperación de contraseña": correo de la cuenta y contraseña nueva dos veces. */
@Composable
fun RecuperacionScreen(
    onListo: (DestinoInicial) -> Unit,
    viewModel: RecuperacionViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    RecuperacionContenido(
        estado = estado,
        onCorreo = viewModel::cambiarCorreo,
        onContrasena = viewModel::cambiarContrasena,
        onConfirmacion = viewModel::cambiarConfirmacion,
        onContinuar = { viewModel.continuar(onListo) },
    )
}

@Composable
fun RecuperacionContenido(
    estado: RecuperacionUiState,
    onCorreo: (String) -> Unit,
    onContrasena: (String) -> Unit,
    onConfirmacion: (String) -> Unit,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlantillaCuenta(modifier) {
        Spacer(Modifier.height(150.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.correo_electronico),
            valor = estado.correo,
            onCambio = onCorreo,
            placeholder = stringResource(Res.string.correo_placeholder),
            error = estado.errores.correo?.mensaje() ?: estado.errorCuenta?.mensaje(),
            teclado = KeyboardType.Email,
        )
        Spacer(Modifier.height(40.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.contrasena_nueva),
            valor = estado.contrasena,
            onCambio = onContrasena,
            placeholder = stringResource(Res.string.contrasena_placeholder, ReglasCuenta.CONTRASENA_MINIMA),
            error = estado.errores.contrasena?.mensaje(),
            esContrasena = true,
        )
        Spacer(Modifier.height(40.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.confirmar_contrasena),
            valor = estado.confirmacion,
            onCambio = onConfirmacion,
            placeholder = stringResource(Res.string.confirmar_placeholder),
            error = estado.errores.confirmacion?.mensaje(),
            esContrasena = true,
            accionTeclado = ImeAction.Done,
        )
        Spacer(Modifier.height(120.dp))
        BotonVelok(
            texto = stringResource(Res.string.continuar),
            onClick = onContinuar,
            cargando = estado.cargando,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Preview
@Composable
private fun RecuperacionContenidoPreview() {
    VelokTheme { RecuperacionContenido(RecuperacionUiState(), {}, {}, {}, {}) }
}
