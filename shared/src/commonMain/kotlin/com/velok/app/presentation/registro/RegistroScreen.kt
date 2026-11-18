package com.velok.app.presentation.registro

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
import com.velok.app.domain.model.ErroresRegistro
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasCuenta
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.CampoCuenta
import com.velok.app.presentation.components.PlantillaCuenta
import com.velok.app.presentation.components.TextoConEnlaces
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.mensaje
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.aceptas_terminos
import velok.shared.generated.resources.conector_y
import velok.shared.generated.resources.confirmar_contrasena
import velok.shared.generated.resources.confirmar_placeholder
import velok.shared.generated.resources.continuar
import velok.shared.generated.resources.contrasena
import velok.shared.generated.resources.contrasena_placeholder
import velok.shared.generated.resources.correo_electronico
import velok.shared.generated.resources.correo_placeholder
import velok.shared.generated.resources.nombre_usuario
import velok.shared.generated.resources.nombre_usuario_placeholder
import velok.shared.generated.resources.privacidad_enlace
import velok.shared.generated.resources.terminos_enlace

/** "Registro con correo electrónico" y su estado con errores. */
@Composable
fun RegistroScreen(
    onTerminos: () -> Unit,
    onPrivacidad: () -> Unit,
    onListo: (DestinoInicial) -> Unit,
    viewModel: RegistroViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    RegistroContenido(
        estado = estado,
        acciones = AccionesRegistro(
            onNombre = viewModel::cambiarNombre,
            onCorreo = viewModel::cambiarCorreo,
            onContrasena = viewModel::cambiarContrasena,
            onConfirmacion = viewModel::cambiarConfirmacion,
            onContinuar = { viewModel.continuar(onListo) },
            onTerminos = onTerminos,
            onPrivacidad = onPrivacidad,
        ),
    )
}

class AccionesRegistro(
    val onNombre: (String) -> Unit = {},
    val onCorreo: (String) -> Unit = {},
    val onContrasena: (String) -> Unit = {},
    val onConfirmacion: (String) -> Unit = {},
    val onContinuar: () -> Unit = {},
    val onTerminos: () -> Unit = {},
    val onPrivacidad: () -> Unit = {},
)

@Composable
fun RegistroContenido(
    estado: RegistroUiState,
    acciones: AccionesRegistro,
    modifier: Modifier = Modifier,
) {
    val placeholderContrasena = stringResource(Res.string.contrasena_placeholder, ReglasCuenta.CONTRASENA_MINIMA)
    PlantillaCuenta(modifier) {
        Spacer(Modifier.height(64.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.nombre_usuario),
            valor = estado.nombre,
            onCambio = acciones.onNombre,
            placeholder = stringResource(Res.string.nombre_usuario_placeholder),
            error = estado.errores.nombre?.mensaje(),
        )
        Spacer(Modifier.height(32.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.correo_electronico),
            valor = estado.correo,
            onCambio = acciones.onCorreo,
            placeholder = stringResource(Res.string.correo_placeholder),
            error = (estado.errores.correo?.mensaje() ?: estado.errorCuenta?.mensaje()),
            teclado = KeyboardType.Email,
        )
        Spacer(Modifier.height(32.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.contrasena),
            valor = estado.contrasena,
            onCambio = acciones.onContrasena,
            placeholder = placeholderContrasena,
            error = estado.errores.contrasena?.mensaje(),
            esContrasena = true,
        )
        Spacer(Modifier.height(32.dp))
        CampoCuenta(
            etiqueta = stringResource(Res.string.confirmar_contrasena),
            valor = estado.confirmacion,
            onCambio = acciones.onConfirmacion,
            placeholder = stringResource(Res.string.confirmar_placeholder),
            error = estado.errores.confirmacion?.mensaje(),
            esContrasena = true,
            accionTeclado = ImeAction.Done,
        )
        Spacer(Modifier.height(48.dp))
        TextoConEnlaces(
            listOf(
                stringResource(Res.string.aceptas_terminos) to null,
                stringResource(Res.string.terminos_enlace) to acciones.onTerminos,
                stringResource(Res.string.conector_y) to null,
                stringResource(Res.string.privacidad_enlace) to acciones.onPrivacidad,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))
        BotonVelok(
            texto = stringResource(Res.string.continuar),
            onClick = acciones.onContinuar,
            cargando = estado.cargando,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Preview
@Composable
private fun RegistroContenidoPreview() {
    VelokTheme { RegistroContenido(RegistroUiState(), AccionesRegistro()) }
}

@Preview
@Composable
private fun RegistroConErrorPreview() {
    VelokTheme {
        RegistroContenido(
            RegistroUiState(
                errores = ErroresRegistro(
                    correo = ErrorCampo.CORREO_INVALIDO,
                    contrasena = ErrorCampo.CONTRASENA_CORTA,
                    confirmacion = ErrorCampo.CONTRASENAS_DISTINTAS,
                ),
            ),
            AccionesRegistro(),
        )
    }
}
