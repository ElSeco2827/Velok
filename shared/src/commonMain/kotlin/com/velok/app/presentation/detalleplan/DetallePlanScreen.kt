package com.velok.app.presentation.detalleplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.velok.app.presentation.components.BotonFlotante
import com.velok.app.presentation.components.EjercicioFila
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.EstadoVacio
import com.velok.app.presentation.components.TituloPantalla
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.nombreVisible
import com.velok.app.presentation.util.resumenSeries
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.editar_plan
import velok.shared.generated.resources.ic_editar
import velok.shared.generated.resources.ic_play
import velok.shared.generated.resources.iniciar_entrenamiento
import velok.shared.generated.resources.plan_no_encontrado
import velok.shared.generated.resources.plan_sin_ejercicios

/** "Información del plan": ejercicios del plan y botón ▶ para empezar el entrenamiento. */
@Composable
fun DetallePlanScreen(
    planId: String,
    onAbrirEjercicio: (String) -> Unit,
    onEditar: () -> Unit,
    onIniciar: () -> Unit,
    viewModel: DetallePlanViewModel = koinViewModel(key = planId) { parametersOf(planId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    DetallePlanContenido(estado, onAbrirEjercicio, onEditar, onIniciar)
}

@Composable
fun DetallePlanContenido(
    estado: DetallePlanUiState,
    onAbrirEjercicio: (String) -> Unit,
    onEditar: () -> Unit,
    onIniciar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val plan = estado.plan
    when {
        estado.cargando -> EstadoCargando(modifier)
        plan == null -> EstadoVacio(stringResource(Res.string.plan_no_encontrado), modifier)
        else -> Box(modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                TituloPantalla(plan.nombreVisible()) {
                    IconButton(onClick = onEditar) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_editar),
                            contentDescription = stringResource(Res.string.editar_plan),
                            tint = VelokTheme.colores.sobreFondo,
                        )
                    }
                }
                if (plan.ejercicios.isEmpty()) {
                    EstadoVacio(stringResource(Res.string.plan_sin_ejercicios))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(plan.ejercicios, key = { i, ep -> "$i-${ep.ejercicio.id}" }) { _, ep ->
                            EjercicioFila(
                                nombre = ep.ejercicio.nombre,
                                detalle = ep.resumenSeries(),
                                imagenUrl = ep.ejercicio.imagenUrl,
                                onClick = { onAbrirEjercicio(ep.ejercicio.id) },
                            )
                        }
                    }
                }
            }
            if (plan.ejercicios.isNotEmpty()) {
                BotonFlotante(
                    icono = Res.drawable.ic_play,
                    descripcion = stringResource(Res.string.iniciar_entrenamiento),
                    onClick = onIniciar,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun DetallePlanContenidoPreview() {
    val press = Ejercicio("1", "Press de banca plano (Barra)", TipoCategoria.PECHO, TipoEquipo.BARRA, Musculo.PECHO, emptyList(), "")
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            DetallePlanContenido(
                estado = DetallePlanUiState(
                    cargando = false,
                    plan = Plan(
                        "1", "Set del Lunes", TipoCategoria.PECHO, "", LocalDate(2026, 10, 12),
                        listOf(EjercicioPlan(press, List(4) { Serie(it + 1, repeticiones = 12) })),
                    ),
                ),
                onAbrirEjercicio = {},
                onEditar = {},
                onIniciar = {},
            )
        }
    }
}
