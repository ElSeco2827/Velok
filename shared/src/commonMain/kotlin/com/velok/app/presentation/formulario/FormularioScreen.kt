package com.velok.app.presentation.formulario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.CampoMedida
import com.velok.app.presentation.components.IndicadorPasos
import com.velok.app.presentation.components.OpcionPildora
import com.velok.app.presentation.components.SelectorRueda
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.diasEnMes
import com.velok.app.presentation.util.mensaje
import com.velok.app.presentation.util.nombre
import com.velok.app.presentation.util.sufijo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ejemplo_estatura
import velok.shared.generated.resources.ejemplo_peso_kg
import velok.shared.generated.resources.ejemplo_peso_lb
import velok.shared.generated.resources.formulario_saludo
import velok.shared.generated.resources.meses
import velok.shared.generated.resources.pregunta_estatura
import velok.shared.generated.resources.pregunta_meta
import velok.shared.generated.resources.pregunta_nacimiento
import velok.shared.generated.resources.pregunta_peso
import velok.shared.generated.resources.pregunta_unidad
import velok.shared.generated.resources.siguiente
import velok.shared.generated.resources.sufijo_cm

/** Años que ofrece la rueda de la fecha de nacimiento. */
private const val ANIOS_EN_RUEDA = 100

/** Cuestionario inicial (diseños "Formulario nacimiento/medidas/estatura/peso/meta"). */
@Composable
fun FormularioScreen(
    onTerminado: () -> Unit,
    viewModel: FormularioViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // "Atrás" del sistema regresa al paso anterior en lugar de salir del cuestionario.
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = estado.paso != PasoFormulario.NACIMIENTO,
        onBackCompleted = { viewModel.atras() },
    )

    FormularioContenido(
        estado = estado,
        acciones = AccionesFormulario(
            onFecha = viewModel::cambiarFecha,
            onUnidad = viewModel::cambiarUnidad,
            onEstatura = viewModel::cambiarEstatura,
            onPeso = viewModel::cambiarPeso,
            onMeta = viewModel::cambiarMeta,
            onSiguiente = { viewModel.siguiente(onTerminado) },
        ),
    )
}

class AccionesFormulario(
    val onFecha: (dia: Int, mes: Int, anio: Int) -> Unit = { _, _, _ -> },
    val onUnidad: (UnidadPeso) -> Unit = {},
    val onEstatura: (String) -> Unit = {},
    val onPeso: (String) -> Unit = {},
    val onMeta: (MetaEntrenamiento) -> Unit = {},
    val onSiguiente: () -> Unit = {},
)

@Composable
fun FormularioContenido(
    estado: FormularioUiState,
    acciones: AccionesFormulario,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(Res.string.formulario_saludo, estado.nombre),
            style = VelokTheme.tipografia.headlineMedium.copy(fontWeight = FontWeight.Normal),
            color = colores.sobreFondo,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        IndicadorPasos(total = PasoFormulario.entries.size, actual = estado.paso.ordinal)

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(estado.paso.pregunta),
                    style = VelokTheme.tipografia.titleLarge.copy(fontWeight = FontWeight.Normal),
                    color = colores.sobreFondo,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(20.dp))
                when (estado.paso) {
                    PasoFormulario.NACIMIENTO -> RuedaFecha(estado, acciones.onFecha)
                    PasoFormulario.UNIDAD -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        UnidadPeso.entries.forEach { unidad ->
                            OpcionPildora(stringResource(unidad.nombre), unidad == estado.unidad, { acciones.onUnidad(unidad) })
                        }
                    }
                    PasoFormulario.ESTATURA -> CampoMedida(
                        valor = estado.estaturaTexto,
                        onCambio = acciones.onEstatura,
                        sufijo = stringResource(Res.string.sufijo_cm),
                        placeholder = stringResource(Res.string.ejemplo_estatura),
                        descripcion = stringResource(Res.string.pregunta_estatura),
                    )
                    PasoFormulario.PESO -> CampoMedida(
                        valor = estado.pesoTexto,
                        onCambio = acciones.onPeso,
                        sufijo = stringResource(estado.unidad.sufijo),
                        placeholder = stringResource(
                            if (estado.unidad == UnidadPeso.KG) Res.string.ejemplo_peso_kg else Res.string.ejemplo_peso_lb,
                        ),
                        descripcion = stringResource(Res.string.pregunta_peso),
                        decimal = true,
                    )
                    PasoFormulario.META -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetaEntrenamiento.entries.forEach { meta ->
                            OpcionPildora(stringResource(meta.nombre), meta == estado.meta, { acciones.onMeta(meta) })
                        }
                    }
                }
                estado.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        it.mensaje(estado.unidad),
                        color = colores.alerta,
                        style = VelokTheme.tipografia.bodySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        BotonVelok(
            texto = stringResource(Res.string.siguiente),
            onClick = acciones.onSiguiente,
            cargando = estado.guardando,
        )
        Spacer(Modifier.height(40.dp))
    }
}

private val PasoFormulario.pregunta
    get() = when (this) {
        PasoFormulario.NACIMIENTO -> Res.string.pregunta_nacimiento
        PasoFormulario.UNIDAD -> Res.string.pregunta_unidad
        PasoFormulario.ESTATURA -> Res.string.pregunta_estatura
        PasoFormulario.PESO -> Res.string.pregunta_peso
        PasoFormulario.META -> Res.string.pregunta_meta
    }

/** Tres ruedas: día, mes y año. */
@Composable
private fun RuedaFecha(estado: FormularioUiState, onFecha: (Int, Int, Int) -> Unit) {
    val fecha = estado.fechaNacimiento
    val meses = stringArrayResource(Res.array.meses)
    val anios = ((estado.hoy.year - ANIOS_EN_RUEDA)..estado.hoy.year).toList()
    val dias = (1..diasEnMes(fecha.year, fecha.month.ordinal + 1)).toList()
    val mes = fecha.month.ordinal + 1

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        SelectorRueda(
            opciones = dias.map { it.toString() },
            seleccionado = fecha.day - 1,
            onSeleccion = { i -> onFecha(dias[i], mes, fecha.year) },
            descripcion = stringResource(Res.string.pregunta_nacimiento),
            modifier = Modifier.width(36.dp),
        )
        SelectorRueda(
            opciones = meses,
            seleccionado = mes - 1,
            onSeleccion = { i -> onFecha(fecha.day, i + 1, fecha.year) },
            descripcion = meses[mes - 1],
            modifier = Modifier.width(110.dp),
        )
        SelectorRueda(
            opciones = anios.map { it.toString() },
            seleccionado = anios.indexOf(fecha.year).coerceAtLeast(0),
            onSeleccion = { i -> onFecha(fecha.day, mes, anios[i]) },
            descripcion = fecha.year.toString(),
            modifier = Modifier.width(56.dp),
        )
    }
}

@Preview
@Composable
private fun FormularioNacimientoPreview() {
    VelokTheme {
        FormularioContenido(
            FormularioUiState(hoy = LocalDate(2026, 11, 4), nombre = "Checo", fechaNacimiento = LocalDate(2004, 4, 25)),
            AccionesFormulario(),
        )
    }
}

@Preview
@Composable
private fun FormularioMetaPreview() {
    VelokTheme {
        FormularioContenido(
            FormularioUiState(
                hoy = LocalDate(2026, 11, 4),
                nombre = "Checo",
                paso = PasoFormulario.META,
                fechaNacimiento = LocalDate(2004, 4, 25),
            ),
            AccionesFormulario(),
        )
    }
}
