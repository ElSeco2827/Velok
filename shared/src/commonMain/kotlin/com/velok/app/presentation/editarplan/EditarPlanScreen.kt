package com.velok.app.presentation.editarplan

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.EjercicioPlan
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.model.TipoSerie
import com.velok.app.presentation.components.BotonAgregarSerie
import com.velok.app.presentation.components.BotonOscuroAncho
import com.velok.app.presentation.components.CampoNota
import com.velok.app.presentation.components.CampoPeso
import com.velok.app.presentation.components.CampoRepeticiones
import com.velok.app.presentation.components.ChipOscuro
import com.velok.app.presentation.components.EjercicioFila
import com.velok.app.presentation.components.EncabezadoEjercicio
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.EtiquetaColumna
import com.velok.app.presentation.components.InsigniaSerie
import com.velok.app.presentation.components.OpcionMenu
import com.velok.app.presentation.components.numerosDeSerie
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatoCronometro
import com.velok.app.presentation.util.resumenSeries
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.seconds
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.agregar_ejercicio
import velok.shared.generated.resources.borrar
import velok.shared.generated.resources.borrar_plan
import velok.shared.generated.resources.borrar_plan_mensaje
import velok.shared.generated.resources.borrar_plan_titulo
import velok.shared.generated.resources.cambiar_tipo_serie
import velok.shared.generated.resources.cancelar
import velok.shared.generated.resources.descanso
import velok.shared.generated.resources.elegir_ejercicio
import velok.shared.generated.resources.eliminar_ejercicio
import velok.shared.generated.resources.ic_cronometro
import velok.shared.generated.resources.ic_nota
import velok.shared.generated.resources.kg
import velok.shared.generated.resources.nombre_plan
import velok.shared.generated.resources.nota
import velok.shared.generated.resources.plan_no_encontrado
import velok.shared.generated.resources.plan_sin_nombre
import velok.shared.generated.resources.reps
import velok.shared.generated.resources.serie
import velok.shared.generated.resources.serie_calentamiento

/** Opciones del selector de descanso, en segundos. */
private val opcionesDescanso = listOf(0, 30, 60, 90, 120, 180)

/** Editor del plan (diseño "Borrar plan"): series por ejercicio, agregar ejercicios y borrar el plan. */
@Composable
fun EditarPlanScreen(
    planId: String,
    onAbrirEjercicio: (String) -> Unit,
    onPlanBorrado: () -> Unit,
    viewModel: EditarPlanViewModel = koinViewModel(key = "editar-$planId") { parametersOf(planId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    EditarPlanContenido(
        estado = estado,
        acciones = AccionesEditarPlan(
            onRenombrar = viewModel::renombrar,
            onAgregarEjercicio = viewModel::agregarEjercicio,
            onEliminarEjercicio = viewModel::eliminarEjercicio,
            onAgregarSerie = viewModel::agregarSerie,
            onAlternarTipoSerie = viewModel::alternarTipoSerie,
            onCambiarPeso = viewModel::cambiarPeso,
            onCambiarRepeticiones = viewModel::cambiarRepeticiones,
            onCambiarDescanso = viewModel::cambiarDescanso,
            onCambiarNota = viewModel::cambiarNota,
            onAbrirEjercicio = onAbrirEjercicio,
            onBorrarPlan = { viewModel.borrarPlan(onBorrado = onPlanBorrado) },
        ),
    )
}

/** Eventos del editor agrupados, para no pasar once parámetros sueltos. */
class AccionesEditarPlan(
    val onRenombrar: (String) -> Unit = {},
    val onAgregarEjercicio: (Ejercicio) -> Unit = {},
    val onEliminarEjercicio: (Int) -> Unit = {},
    val onAgregarSerie: (Int) -> Unit = {},
    val onAlternarTipoSerie: (Int, Int) -> Unit = { _, _ -> },
    val onCambiarPeso: (Int, Int, Double?) -> Unit = { _, _, _ -> },
    val onCambiarRepeticiones: (Int, Int, Int?) -> Unit = { _, _, _ -> },
    val onCambiarDescanso: (Int, Int) -> Unit = { _, _ -> },
    val onCambiarNota: (Int, String) -> Unit = { _, _ -> },
    val onAbrirEjercicio: (String) -> Unit = {},
    val onBorrarPlan: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlanContenido(
    estado: EditarPlanUiState,
    acciones: AccionesEditarPlan,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val plan = estado.plan
    var elegirEjercicio by rememberSaveable { mutableStateOf(false) }
    var confirmarBorrado by rememberSaveable { mutableStateOf(false) }

    Box(modifier.fillMaxSize().background(colores.fondoProfundo)) {
        when {
            estado.cargando -> EstadoCargando()
            plan == null -> EstadoVacio(stringResource(Res.string.plan_no_encontrado))
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "nombre") { CampoNombrePlan(plan.nombre, acciones.onRenombrar) }
                itemsIndexed(plan.ejercicios, key = { i, ep -> "$i-${ep.ejercicio.id}" }) { indice, ep ->
                    EjercicioEditable(indice, ep, acciones)
                }
                item(key = "agregar") {
                    BotonOscuroAncho(
                        texto = stringResource(Res.string.agregar_ejercicio),
                        onClick = { elegirEjercicio = true },
                    )
                }
                item(key = "borrar") {
                    TextButton(onClick = { confirmarBorrado = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(Res.string.borrar_plan), color = colores.alerta)
                    }
                }
            }
        }
    }

    if (elegirEjercicio) {
        ModalBottomSheet(
            onDismissRequest = { elegirEjercicio = false },
            containerColor = colores.fondo,
            shape = VelokTheme.formasVelok.hojaInferior,
        ) {
            Text(
                text = stringResource(Res.string.elegir_ejercicio),
                style = VelokTheme.tipografia.titleMedium,
                color = colores.sobreFondo,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(estado.catalogo, key = { it.id }) { ejercicio ->
                    EjercicioFila(
                        nombre = ejercicio.nombre,
                        detalle = ejercicio.resumenSeries(),
                        imagenUrl = ejercicio.imagenUrl,
                        onClick = {
                            acciones.onAgregarEjercicio(ejercicio)
                            elegirEjercicio = false
                        },
                    )
                }
            }
        }
    }

    if (confirmarBorrado && plan != null) {
        val nombre = plan.nombre.ifBlank { stringResource(Res.string.plan_sin_nombre) }
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text(stringResource(Res.string.borrar_plan_titulo)) },
            text = { Text(stringResource(Res.string.borrar_plan_mensaje, nombre)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    acciones.onBorrarPlan()
                }) { Text(stringResource(Res.string.borrar), color = colores.alerta) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) {
                    Text(stringResource(Res.string.cancelar), color = colores.textoPrincipal)
                }
            },
            containerColor = colores.superficie,
        )
    }
}

@Composable
private fun CampoNombrePlan(nombre: String, onCambio: (String) -> Unit) {
    val colores = VelokTheme.colores
    val estilo = VelokTheme.tipografia.titleLarge.copy(color = colores.sobreFondo)
    Column {
        BasicTextField(
            value = nombre,
            onValueChange = onCambio,
            singleLine = true,
            textStyle = estilo,
            cursorBrush = SolidColor(colores.acento),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            decorationBox = { campo ->
                Box {
                    if (nombre.isEmpty()) {
                        Text(stringResource(Res.string.nombre_plan), style = estilo.copy(color = colores.sobreFondoSecundario))
                    }
                    campo()
                }
            },
        )
        HorizontalDivider(color = colores.bordeOscuro)
    }
}

@Composable
private fun EjercicioEditable(indice: Int, ep: EjercicioPlan, acciones: AccionesEditarPlan) {
    var mostrarNota by remember(ep.ejercicio.id) { mutableStateOf(ep.nota.isNotEmpty()) }
    var menuDescanso by remember { mutableStateOf(false) }

    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EncabezadoEjercicio(
            nombre = ep.ejercicio.nombre,
            imagenUrl = ep.ejercicio.imagenUrl,
            onNombreClick = { acciones.onAbrirEjercicio(ep.ejercicio.id) },
            opciones = listOf(
                OpcionMenu(stringResource(Res.string.eliminar_ejercicio)) { acciones.onEliminarEjercicio(indice) },
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                ChipOscuro(
                    icono = Res.drawable.ic_cronometro,
                    texto = stringResource(Res.string.descanso, ep.descansoSegundos.seconds.formatoCronometro()),
                    onClick = { menuDescanso = true },
                )
                DropdownMenu(
                    expanded = menuDescanso,
                    onDismissRequest = { menuDescanso = false },
                    containerColor = VelokTheme.colores.superficie,
                ) {
                    opcionesDescanso.forEach { segundos ->
                        DropdownMenuItem(
                            text = { Text(segundos.seconds.formatoCronometro(), color = VelokTheme.colores.textoPrincipal) },
                            onClick = {
                                menuDescanso = false
                                acciones.onCambiarDescanso(indice, segundos)
                            },
                        )
                    }
                }
            }
            ChipOscuro(
                icono = Res.drawable.ic_nota,
                texto = stringResource(Res.string.nota),
                onClick = { mostrarNota = !mostrarNota },
                destacado = mostrarNota,
            )
        }
        if (mostrarNota) {
            CampoNota(ep.nota, onCambio = { acciones.onCambiarNota(indice, it) })
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EtiquetaColumna(stringResource(Res.string.serie), Modifier.width(40.dp))
            EtiquetaColumna(stringResource(Res.string.kg), Modifier.weight(1f))
            EtiquetaColumna(stringResource(Res.string.reps), Modifier.weight(1f))
        }
        val numeros = numerosDeSerie(ep.series.map { it.tipo })
        ep.series.forEachIndexed { i, serie ->
            FilaSerieEditable(
                numero = numeros[i],
                serie = serie,
                onAlternarTipo = { acciones.onAlternarTipoSerie(indice, serie.id) },
                onPeso = { acciones.onCambiarPeso(indice, serie.id, it) },
                onRepeticiones = { acciones.onCambiarRepeticiones(indice, serie.id, it) },
            )
        }
        Spacer(Modifier.height(2.dp))
        BotonAgregarSerie(onClick = { acciones.onAgregarSerie(indice) })
    }
}

@Composable
private fun FilaSerieEditable(
    numero: Int,
    serie: Serie,
    onAlternarTipo: () -> Unit,
    onPeso: (Double?) -> Unit,
    onRepeticiones: (Int?) -> Unit,
) {
    val etiqueta = if (serie.tipo == TipoSerie.CALENTAMIENTO) stringResource(Res.string.serie_calentamiento) else numero.toString()
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            InsigniaSerie(
                numero = numero,
                tipo = serie.tipo,
                onClick = onAlternarTipo,
                descripcion = stringResource(Res.string.cambiar_tipo_serie, etiqueta),
            )
        }
        CampoPeso(serie.pesoKg, onPeso, Modifier.weight(1f))
        CampoRepeticiones(serie.repeticiones, onRepeticiones, Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun EditarPlanContenidoPreview() {
    val sentadilla = Ejercicio("s", "Sentadilla (Barra)", TipoCategoria.PIERNA, TipoEquipo.BARRA, Musculo.CUADRICEPS, emptyList(), "")
    val series = listOf(
        Serie(1, pesoKg = 10.0, repeticiones = 12),
        Serie(2, pesoKg = 10.0, repeticiones = 12),
        Serie(3, TipoSerie.CALENTAMIENTO, null, 10),
    )
    VelokTheme {
        Surface(color = VelokTheme.colores.fondoProfundo) {
            EditarPlanContenido(
                estado = EditarPlanUiState(
                    cargando = false,
                    plan = Plan("p", "Set del Miércoles", TipoCategoria.PIERNA, "", LocalDate(2026, 10, 16), listOf(EjercicioPlan(sentadilla, series))),
                ),
                acciones = AccionesEditarPlan(),
            )
        }
    }
}
