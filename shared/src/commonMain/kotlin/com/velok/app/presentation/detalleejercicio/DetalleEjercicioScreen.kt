package com.velok.app.presentation.detalleejercicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.TarjetaResumen
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.nombre
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ejercicio_no_encontrado
import velok.shared.generated.resources.primario
import velok.shared.generated.resources.resumen_info_ejercicio
import velok.shared.generated.resources.secundario

/** "Información del ejercicio": imagen, músculos trabajados y progreso en este ejercicio. */
@Composable
fun DetalleEjercicioScreen(
    ejercicioId: String,
    viewModel: DetalleEjercicioViewModel = koinViewModel(key = "ejercicio-$ejercicioId") { parametersOf(ejercicioId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    DetalleEjercicioContenido(estado, viewModel::cambiarMetrica, viewModel::cambiarRango)
}

@Composable
fun DetalleEjercicioContenido(
    estado: DetalleEjercicioUiState,
    onMetrica: (MetricaProgreso) -> Unit,
    onRango: (RangoTiempo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val ejercicio = estado.ejercicio
    Box(modifier.fillMaxSize().background(colores.fondoProfundo)) {
        when {
            estado.cargando -> EstadoCargando()
            ejercicio == null -> EstadoVacio(stringResource(Res.string.ejercicio_no_encontrado))
            else -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                AsyncImage(
                    model = ejercicio.imagenUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(colores.superficie),
                    error = ColorPainter(colores.superficie),
                    modifier = Modifier.fillMaxWidth().height(220.dp).background(colores.superficie),
                )
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = ejercicio.nombre,
                        style = VelokTheme.tipografia.headlineMedium,
                        color = colores.sobreFondo,
                        modifier = Modifier.semantics { heading() },
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.primario, stringResource(ejercicio.musculoPrimario.nombre)),
                        style = VelokTheme.tipografia.bodyMedium,
                        color = colores.sobreFondoSecundario,
                    )
                    if (ejercicio.musculosSecundarios.isNotEmpty()) {
                        val secundarios = ejercicio.musculosSecundarios.map { stringResource(it.nombre) }.joinToString(", ")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.secundario, secundarios),
                            style = VelokTheme.tipografia.bodyMedium,
                            color = colores.sobreFondoSecundario,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    TarjetaResumen(
                        metrica = estado.metrica,
                        rango = estado.rango,
                        valores = estado.progreso,
                        explicacion = stringResource(Res.string.resumen_info_ejercicio),
                        onMetrica = onMetrica,
                        onRango = onRango,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetalleEjercicioContenidoPreview() {
    VelokTheme {
        Surface(color = VelokTheme.colores.fondoProfundo) {
            DetalleEjercicioContenido(
                estado = DetalleEjercicioUiState(
                    cargando = false,
                    ejercicio = Ejercicio(
                        "s", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA,
                        Musculo.CUADRICEPS, listOf(Musculo.ISQUIOTIBIALES, Musculo.GLUTEOS), "",
                    ),
                    progreso = listOf(5.0, 7.5, 7.5, 10.0, 10.0, 12.5),
                ),
                onMetrica = {},
                onRango = {},
            )
        }
    }
}
