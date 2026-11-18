package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private val ALTO_FILA = 32.dp

/**
 * Rueda vertical de opciones (como en "Formulario nacimiento"): muestra tres filas y la del centro
 * es la seleccionada. Se puede deslizar o tocar una fila.
 */
@Composable
fun SelectorRueda(
    opciones: List<String>,
    seleccionado: Int,
    onSeleccion: (Int) -> Unit,
    descripcion: String,
    modifier: Modifier = Modifier,
    altoFila: Dp = ALTO_FILA,
) {
    val colores = VelokTheme.colores
    val estado = rememberLazyListState(initialFirstVisibleItemIndex = seleccionado)
    val alcance = rememberCoroutineScope()
    val altoFilaPx = with(LocalDensity.current) { altoFila.toPx() }
    val alSeleccionar by rememberUpdatedState(onSeleccion)

    // Fila que queda en el centro según cuánto se ha desplazado la lista.
    val centro by remember {
        derivedStateOf {
            val extra = if (estado.firstVisibleItemScrollOffset > altoFilaPx / 2) 1 else 0
            (estado.firstVisibleItemIndex + extra).coerceIn(0, (opciones.size - 1).coerceAtLeast(0))
        }
    }

    // Al soltar, avisa la fila del centro.
    LaunchedEffect(estado) {
        snapshotFlow { estado.isScrollInProgress }
            .distinctUntilChanged()
            .filter { enMovimiento -> !enMovimiento }
            .collect { alSeleccionar(centro) }
    }
    // Si el valor cambia desde fuera (por ejemplo, un mes más corto), la rueda se acomoda.
    LaunchedEffect(seleccionado, opciones.size) {
        if (!estado.isScrollInProgress && centro != seleccionado) {
            estado.scrollToItem(seleccionado.coerceIn(0, (opciones.size - 1).coerceAtLeast(0)))
        }
    }

    LazyColumn(
        state = estado,
        flingBehavior = rememberSnapFlingBehavior(estado),
        contentPadding = PaddingValues(vertical = altoFila),
        modifier = modifier
            .height(altoFila * 3)
            .semantics { contentDescription = descripcion },
    ) {
        itemsIndexed(opciones) { i, texto ->
            val elegido = i == centro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoFila)
                    .semantics { selected = elegido }
                    .clickable(role = Role.Button) { alcance.launch { estado.animateScrollToItem(i) } },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = texto,
                    style = VelokTheme.tipografia.bodyLarge,
                    color = if (elegido) colores.sobreFondo else colores.sobreFondoSecundario.copy(alpha = 0.6f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectorRuedaPreview() {
    VelokTheme {
        Row(
            Modifier.background(VelokTheme.colores.fondo).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SelectorRueda((1..31).map { it.toString() }, 24, {}, "Día", Modifier.width(40.dp))
            SelectorRueda(listOf("Marzo", "Abril", "Mayo"), 1, {}, "Mes", Modifier.width(90.dp))
            SelectorRueda((2000..2010).map { it.toString() }, 4, {}, "Año", Modifier.width(56.dp))
        }
    }
}
