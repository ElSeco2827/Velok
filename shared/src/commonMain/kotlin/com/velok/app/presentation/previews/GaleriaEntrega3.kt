package com.velok.app.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.ErroresIngreso
import com.velok.app.domain.model.ErroresRegistro
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.presentation.bienvenida.BienvenidaContenido
import com.velok.app.presentation.bienvenida.BienvenidaUiState
import com.velok.app.presentation.carga.CargaContenido
import com.velok.app.presentation.editarplan.AccionesEditarPlan
import com.velok.app.presentation.editarplan.EditarPlanContenido
import com.velok.app.presentation.editarplan.EditarPlanUiState
import com.velok.app.presentation.formulario.AccionesFormulario
import com.velok.app.presentation.formulario.FormularioContenido
import com.velok.app.presentation.formulario.FormularioUiState
import com.velok.app.presentation.formulario.PasoFormulario
import com.velok.app.presentation.ingreso.IngresoContenido
import com.velok.app.presentation.ingreso.IngresoUiState
import com.velok.app.presentation.legal.DocumentoLegal
import com.velok.app.presentation.legal.DocumentoLegalScreen
import com.velok.app.presentation.recuperacion.RecuperacionContenido
import com.velok.app.presentation.recuperacion.RecuperacionUiState
import com.velok.app.presentation.registro.AccionesRegistro
import com.velok.app.presentation.registro.RegistroContenido
import com.velok.app.presentation.registro.RegistroUiState
import com.velok.app.presentation.theme.VelokTheme
import kotlinx.datetime.LocalDate

// Galería para presentar las pantallas de la entrega 3 sin recorrer el flujo.
// Ábrela en Android Studio con la vista "Split" o "Design". Cada vista previa lleva el nombre de
// su imagen en docs/diseno y el tamaño de los diseños (393 x 852).

private const val GRUPO = "Entrega 3"
private const val ANCHO = 393
private const val ALTO = 852

private val hoy = LocalDate(2026, 11, 4)
private val nacimiento = LocalDate(2004, 4, 25)

private fun formulario(paso: PasoFormulario, estatura: String = "", peso: String = "", error: ErrorCampo? = null) =
    FormularioUiState(
        hoy = hoy,
        nombre = "Checo",
        paso = paso,
        fechaNacimiento = nacimiento,
        unidad = UnidadPeso.KG,
        estaturaTexto = estatura,
        pesoTexto = peso,
        meta = MetaEntrenamiento.PERDER_GRASA,
        error = error,
    )

// ---------------------------------------------------------------- inicio y cuenta

@Preview(name = "Pagina inicial (Cargando)", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun CargaPreview() = VelokTheme { CargaContenido() }

@Preview(name = "Seleccion de inicio", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun SeleccionInicioPreview() = VelokTheme { BienvenidaContenido(BienvenidaUiState(), {}, {}, {}) }

@Preview(name = "Inicio de sesion", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun InicioSesionPreview() = VelokTheme { IngresoContenido(IngresoUiState(), {}, {}, {}, {}, {}) }

@Preview(name = "Inicio de sesion con error", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun InicioSesionErrorPreview() = VelokTheme {
    IngresoContenido(
        IngresoUiState(
            correo = "ejemplo",
            contrasena = "1234",
            errores = ErroresIngreso(ErrorCampo.CORREO_INVALIDO, ErrorCampo.CONTRASENA_CORTA),
        ),
        {}, {}, {}, {}, {},
    )
}

@Preview(name = "Inicio de sesion con credenciales incorrectas", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun InicioSesionCredencialesPreview() = VelokTheme {
    IngresoContenido(
        IngresoUiState(correo = "checo@velok.app", contrasena = "incorrecta1", errorCuenta = ErrorCuenta.CREDENCIALES_INCORRECTAS),
        {}, {}, {}, {}, {},
    )
}

@Preview(name = "Registro con correo electronico", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun RegistroPreview() = VelokTheme { RegistroContenido(RegistroUiState(), AccionesRegistro()) }

@Preview(name = "Registro con correo error", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun RegistroErrorPreview() = VelokTheme {
    RegistroContenido(
        RegistroUiState(
            nombre = "Checo",
            correo = "ejemplo@gmail",
            contrasena = "1234",
            confirmacion = "4321",
            errores = ErroresRegistro(
                correo = ErrorCampo.CORREO_INVALIDO,
                contrasena = ErrorCampo.CONTRASENA_CORTA,
                confirmacion = ErrorCampo.CONTRASENAS_DISTINTAS,
            ),
        ),
        AccionesRegistro(),
    )
}

@Preview(name = "Recuperacion de contrasena", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun RecuperacionPreview() = VelokTheme { RecuperacionContenido(RecuperacionUiState(), {}, {}, {}, {}) }

@Preview(name = "Terminos y condiciones", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun TerminosPreview() = VelokTheme { DocumentoLegalScreen(DocumentoLegal.TERMINOS, onContinuar = {}) }

@Preview(name = "Politicas de privacidad", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun PrivacidadPreview() = VelokTheme { DocumentoLegalScreen(DocumentoLegal.PRIVACIDAD, onContinuar = {}) }

// ---------------------------------------------------------------- cuestionario

@Preview(name = "Formulario nacimiento", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioNacimientoPreview() = VelokTheme {
    FormularioContenido(formulario(PasoFormulario.NACIMIENTO), AccionesFormulario())
}

@Preview(name = "Formulario medidas", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioMedidasPreview() = VelokTheme {
    FormularioContenido(formulario(PasoFormulario.UNIDAD), AccionesFormulario())
}

@Preview(name = "Formulario estatura", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioEstaturaPreview() = VelokTheme {
    FormularioContenido(formulario(PasoFormulario.ESTATURA, estatura = "170"), AccionesFormulario())
}

@Preview(name = "Formulario peso", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioPesoPreview() = VelokTheme {
    FormularioContenido(formulario(PasoFormulario.PESO, estatura = "170", peso = "100"), AccionesFormulario())
}

@Preview(name = "Formulario peso con error", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioPesoErrorPreview() = VelokTheme {
    FormularioContenido(
        formulario(PasoFormulario.PESO, estatura = "170", peso = "20", error = ErrorCampo.PESO_FUERA_DE_RANGO),
        AccionesFormulario(),
    )
}

@Preview(name = "Formulario meta", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun FormularioMetaPreview() = VelokTheme {
    FormularioContenido(formulario(PasoFormulario.META, estatura = "170", peso = "100"), AccionesFormulario())
}

// ---------------------------------------------------------------- editor

@Preview(name = "Editar ejercicio", group = GRUPO, widthDp = ANCHO, heightDp = ALTO)
@Composable
private fun EditarEjercicioPreview() = VelokTheme {
    val sentadilla = Ejercicio(
        "sentadilla-barra", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA,
        Musculo.CUADRICEPS, listOf(Musculo.ISQUIOTIBIALES, Musculo.GLUTEOS), "",
    )
    val series = listOf(
        Serie(1, pesoKg = 10.0, repeticiones = 12),
        Serie(2, pesoKg = 10.0, repeticiones = 12),
        Serie(3, pesoKg = 10.0, repeticiones = 15),
        Serie(4, pesoKg = 10.0, repeticiones = 15),
        Serie(5, TipoSerie.CALENTAMIENTO, null, 10),
        Serie(6, TipoSerie.CALENTAMIENTO, 5.0, 10),
    )
    EditarPlanContenido(
        EditarPlanUiState(
            cargando = false,
            plan = Plan("plan-miercoles", "Set del Miércoles", TipoCategoria.PIERNA, "", nacimiento,
                listOf(EjercicioPlan(sentadilla, series))),
        ),
        AccionesEditarPlan(),
    )
}
