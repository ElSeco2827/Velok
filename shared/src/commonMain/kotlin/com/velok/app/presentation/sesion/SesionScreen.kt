package com.velok.app.presentation.sesion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.EjercicioSesion
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.SerieSesion
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.iniciarSesion
import com.velok.app.presentation.components.BotonAgregarSerie
import com.velok.app.presentation.components.BotonCompletarSerie
import com.velok.app.presentation.components.CampoNota
import com.velok.app.presentation.components.CampoPeso
import com.velok.app.presentation.components.CampoRepeticiones
import com.velok.app.presentation.components.ChipOscuro
import com.velok.app.presentation.components.EncabezadoEjercicio
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.EtiquetaColumna
import com.velok.app.presentation.components.InsigniaSerie
import com.velok.app.presentation.components.OpcionMenu
import com.velok.app.presentation.components.numerosDeSerie
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatearMiles
import com.velok.app.presentation.util.formatearNumero
import com.velok.app.presentation.util.formatoCronometro
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.anterior
import velok.shared.generated.resources.descanso
import velok.shared.generated.resources.desmarcar_serie
import velok.shared.generated.resources.duracion
import velok.shared.generated.resources.eliminar_ejercicio
import velok.shared.generated.resources.ic_cronometro
import velok.shared.generated.resources.ic_nota
import velok.shared.generated.resources.kg
import velok.shared.generated.resources.marcar_serie
import velok.shared.generated.resources.nota
import velok.shared.generated.resources.plan_no_encontrado
import velok.shared.generated.resources.reps
import velok.shared.generated.resources.salir
import velok.shared.generated.resources.salir_sin_guardar_mensaje
import velok.shared.generated.resources.salir_sin_guardar_titulo
import velok.shared.generated.resources.seguir_entrenando
import velok.shared.generated.resources.serie
import velok.shared.generated.resources.serie_calentamiento
import velok.shared.generated.resources.series
import velok.shared.generated.resources.sin_valor
import velok.shared.generated.resources.terminar_entrenamiento
import velok.shared.generated.resources.valor_anterior
import velok.shared.generated.resources.volumen
import velok.shared.generated.resources.volumen_kg

/** "Ejercicio en progreso": cronómetro, volumen, series y tabla por ejercicio para ir marcando. */
@Composable
fun SesionScreen(
    planId: String,
    onAbrirEjercicio: (String) -> Unit,
    onSalir: () -> Unit,
    viewModel: SesionViewModel = koinViewModel(key = "sesion-$planId") { parametersOf(planId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    SesionContenido(
        estado = estado,
        acciones = AccionesSesion(
            onAlternarCompletada = viewModel::alternarCompletada,
            onCambiarPeso = viewModel::cambiarPeso,
            onCambiarRepeticiones = viewModel::cambiarRepeticiones,
            onAgregarSerie = viewModel::agregarSerie,
            onCambiarNota = viewModel::cambiarNota,
            onEliminarEjercicio = viewModel::eliminarEjercicio,
            onAbrirEjercicio = onAbrirEjercicio,
            onTerminar = { viewModel.terminar(onTerminada = onSalir) },
            onCancelarSalida = viewModel::cancelarSalida,
            onOmitirDescanso = viewModel::omitirDescanso,
            onSalirSinGuardar = onSalir,
        ),
    )
}

/** Eventos de la sesión agrupados. */
class AccionesSesion(
    val onAlternarCompletada: (Int, Int) -> Unit = { _, _ -> },
    val onCambiarPeso: (Int, Int, Double?) -> Unit = { _, _, _ -> },
    val onCambiarRepeticiones: (Int, Int, Int?) -> Unit = { _, _, _ -> },
    val onAgregarSerie: (Int) -> Unit = {},
    val onCambiarNota: (Int, String) -> Unit = { _, _ -> },
    val onEliminarEjercicio: (Int) -> Unit = {},
    val onAbrirEjercicio: (String) -> Unit = {},
    val onTerminar: () -> Unit = {},
    val onCancelarSalida: () -> Unit = {},
    val onOmitirDescanso: () -> Unit = {},
    val onSalirSinGuardar: () -> Unit = {},
)

@Composable
fun SesionContenido(
    estado: SesionUiState,
    acciones: AccionesSesion,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val sesion = estado.sesion
    Box(modifier.fillMaxSize().background(colores.fondoProfundo)) {
        when {
            estado.cargando -> EstadoCargando()
            sesion == null -> EstadoVacio(stringResource(Res.string.plan_no_encontrado))
            else -> Column(Modifier.fillMaxSize()) {
                ResumenSesion(
                    duracion = sesion.duracion(estado.ahora).formatoCronometro(),
                    volumen = stringResource(Res.string.volumen_kg, formatearMiles(sesion.volumenKg.roundToLong())),
                    series = sesion.seriesCompletadas.toString(),
                )
                HorizontalDivider(color = colores.superficieOscura)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    itemsIndexed(sesion.ejercicios, key = { i, ej -> "$i-${ej.ejercicio.id}" }) { indice, ej ->
                        val restante = estado.descanso
                            ?.takeIf { it.indiceEjercicio == indice }
                            ?.let { it.hasta - estado.ahora }
                        EjercicioEnSesion(indice, ej, restante?.inWholeSeconds?.toInt(), acciones)
                    }
                    item(key = "terminar") {
                        Button(
                            onClick = acciones.onTerminar,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = VelokTheme.formas.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.acento,
                                contentColor = colores.sobreAcento,
                            ),
                        ) {
                            Text(stringResource(Res.string.terminar_entrenamiento), style = VelokTheme.tipografia.titleSmall)
                        }
                    }
                }
            }
        }
    }

    if (estado.confirmarSalida) {
        AlertDialog(
            onDismissRequest = acciones.onCancelarSalida,
            title = { Text(stringResource(Res.string.salir_sin_guardar_titulo)) },
            text = { Text(stringResource(Res.string.salir_sin_guardar_mensaje)) },
            confirmButton = {
                TextButton(onClick = acciones.onSalirSinGuardar) {
                    Text(stringResource(Res.string.salir), color = colores.alerta)
                }
            },
            dismissButton = {
                TextButton(onClick = acciones.onCancelarSalida) {
                    Text(stringResource(Res.string.seguir_entrenando), color = colores.textoPrincipal)
                }
            },
            containerColor = colores.superficie,
        )
    }
}

@Composable
private fun ResumenSesion(duracion: String, volumen: String, series: String) {
    val colores = VelokTheme.colores
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colores.encabezado)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        DatoSesion(stringResource(Res.string.duracion), duracion, colores.informativo, Modifier.weight(1f))
        DatoSesion(stringResource(Res.string.volumen), volumen, colores.sobreFondo, Modifier.weight(1f))
        DatoSesion(stringResource(Res.string.series), series, colores.sobreFondo, Modifier.weight(1f))
    }
}

@Composable
private fun DatoSesion(etiqueta: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(etiqueta, style = VelokTheme.tipografia.bodySmall, color = VelokTheme.colores.sobreFondoSecundario)
        Text(valor, style = VelokTheme.tipografia.bodyLarge, color = color)
    }
}

@Composable
private fun EjercicioEnSesion(
    indice: Int,
    ej: EjercicioSesion,
    descansoRestanteSegundos: Int?,
    acciones: AccionesSesion,
) {
    var mostrarNota by remember(ej.ejercicio.id) { mutableStateOf(ej.nota.isNotEmpty()) }
    val textoDescanso = (descansoRestanteSegundos ?: ej.descansoSegundos).seconds.formatoCronometro()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EncabezadoEjercicio(
            nombre = ej.ejercicio.nombre,
            imagenUrl = ej.ejercicio.imagenUrl,
            onNombreClick = { acciones.onAbrirEjercicio(ej.ejercicio.id) },
            opciones = listOf(
                OpcionMenu(stringResource(Res.string.eliminar_ejercicio)) { acciones.onEliminarEjercicio(indice) },
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Muestra la cuenta regresiva en verde mientras corre el descanso; tocarlo lo termina.
            ChipOscuro(
                icono = Res.drawable.ic_cronometro,
                texto = stringResource(Res.string.descanso, textoDescanso),
                onClick = acciones.onOmitirDescanso,
                destacado = descansoRestanteSegundos != null,
            )
            ChipOscuro(
                icono = Res.drawable.ic_nota,
                texto = stringResource(Res.string.nota),
                onClick = { mostrarNota = !mostrarNota },
                destacado = mostrarNota,
            )
        }
        if (mostrarNota) {
            CampoNota(ej.nota, onCambio = { acciones.onCambiarNota(indice, it) })
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EtiquetaColumna(stringResource(Res.string.serie), Modifier.width(40.dp))
            EtiquetaColumna(stringResource(Res.string.anterior), Modifier.weight(1.3f))
            EtiquetaColumna(stringResource(Res.string.kg), Modifier.weight(1f))
            EtiquetaColumna(stringResource(Res.string.reps), Modifier.weight(1f))
            Spacer(Modifier.width(38.dp))
        }
        val numeros = numerosDeSerie(ej.series.map { it.tipo })
        ej.series.forEachIndexed { i, serie ->
            FilaSerieSesion(
                numero = numeros[i],
                serie = serie,
                onPeso = { acciones.onCambiarPeso(indice, serie.id, it) },
                onRepeticiones = { acciones.onCambiarRepeticiones(indice, serie.id, it) },
                onCompletar = { acciones.onAlternarCompletada(indice, serie.id) },
            )
        }
        Spacer(Modifier.height(2.dp))
        BotonAgregarSerie(onClick = { acciones.onAgregarSerie(indice) })
    }
}

@Composable
private fun FilaSerieSesion(
    numero: Int,
    serie: SerieSesion,
    onPeso: (Double?) -> Unit,
    onRepeticiones: (Int?) -> Unit,
    onCompletar: () -> Unit,
) {
    val colores = VelokTheme.colores
    val anterior = serie.anterior?.takeIf { it.repeticiones != null }
        ?.let { stringResource(Res.string.valor_anterior, formatearNumero(it.pesoKg ?: 0.0), it.repeticiones ?: 0) }
        ?: stringResource(Res.string.sin_valor)
    val etiqueta = if (serie.tipo == TipoSerie.CALENTAMIENTO) stringResource(Res.string.serie_calentamiento) else numero.toString()

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            InsigniaSerie(numero, serie.tipo)
        }
        Text(
            text = anterior,
            style = VelokTheme.tipografia.bodySmall,
            color = colores.sobreFondoSecundario,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.3f),
        )
        CampoPeso(serie.pesoKg, onPeso, Modifier.weight(1f))
        CampoRepeticiones(serie.repeticiones, onRepeticiones, Modifier.weight(1f))
        BotonCompletarSerie(
            completada = serie.completada,
            descripcion = if (serie.completada) {
                stringResource(Res.string.desmarcar_serie, etiqueta)
            } else {
                stringResource(Res.string.marcar_serie, etiqueta)
            },
            onClick = onCompletar,
        )
    }
}

@Preview
@Composable
private fun SesionContenidoPreview() {
    val sentadilla = Ejercicio("s", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA, Musculo.CUADRICEPS, emptyList(), "")
    val plan = Plan(
        "p", "Set del Miércoles", TipoCategoria.PIERNA, "", LocalDate(2026, 10, 16),
        listOf(EjercicioPlan(sentadilla, listOf(Serie(1, pesoKg = 10.0, repeticiones = 12), Serie(2, TipoSerie.CALENTAMIENTO, 5.0, 10)))),
    )
    val inicio = Instant.fromEpochSeconds(0)
    VelokTheme {
        Surface(color = VelokTheme.colores.fondoProfundo) {
            SesionContenido(
                estado = SesionUiState(cargando = false, sesion = plan.iniciarSesion("s", inicio), ahora = inicio + 12.seconds),
                acciones = AccionesSesion(),
            )
        }
    }
}
