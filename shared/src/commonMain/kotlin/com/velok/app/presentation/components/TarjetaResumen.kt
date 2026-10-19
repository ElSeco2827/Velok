package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.nombre
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.entendido
import velok.shared.generated.resources.ic_flecha_abajo
import velok.shared.generated.resources.ic_info
import velok.shared.generated.resources.resumen
import velok.shared.generated.resources.sin_datos_progreso

/**
 * Tarjeta "Resumen": selector de métrica, selector de periodo y gráfica de línea.
 *
 * @param explicacion texto del botón (i).
 */
@Composable
fun TarjetaResumen(
    metrica: MetricaProgreso,
    rango: RangoTiempo,
    valores: List<Double>,
    explicacion: String,
    onMetrica: (MetricaProgreso) -> Unit,
    onRango: (RangoTiempo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    var mostrarInfo by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = VelokTheme.formas.small,
        color = colores.superficie,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.resumen),
                    style = VelokTheme.tipografia.titleMedium,
                    color = colores.textoPrincipal,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { mostrarInfo = true }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_info),
                        contentDescription = explicacion,
                        tint = colores.textoSecundario,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorDesplegable(
                    opciones = MetricaProgreso.entries,
                    seleccion = metrica,
                    etiqueta = { stringResource(it.nombre) },
                    onSeleccion = onMetrica,
                    modifier = Modifier.weight(1f),
                )
                SelectorDesplegable(
                    opciones = RangoTiempo.entries,
                    seleccion = rango,
                    etiqueta = { stringResource(it.nombre) },
                    onSeleccion = onRango,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                GraficaLinea(valores, Modifier.fillMaxWidth().height(160.dp))
                if (valores.size < 2) {
                    Text(
                        text = stringResource(Res.string.sin_datos_progreso),
                        style = VelokTheme.tipografia.bodySmall,
                        color = colores.textoSecundario,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }

    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            confirmButton = {
                TextButton(onClick = { mostrarInfo = false }) {
                    Text(stringResource(Res.string.entendido), color = colores.acentoTexto)
                }
            },
            title = { Text(stringResource(Res.string.resumen)) },
            text = { Text(explicacion) },
            containerColor = colores.superficie,
        )
    }
}

/** Caja con borde y flecha que abre un menú de opciones ("Peso ▾", "3 días ▾"). */
@Composable
fun <T> SelectorDesplegable(
    opciones: List<T>,
    seleccion: T,
    etiqueta: @Composable (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    var abierto by remember { mutableStateOf(false) }
    Box(modifier) {
        Surface(
            onClick = { abierto = true },
            shape = VelokTheme.formas.extraSmall,
            color = colores.superficie,
            border = BorderStroke(1.dp, colores.borde),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = etiqueta(seleccion),
                    style = VelokTheme.tipografia.bodySmall,
                    color = colores.textoPrincipal,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_flecha_abajo),
                    contentDescription = null,
                    tint = colores.textoPrincipal,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false },
            containerColor = colores.superficie,
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(etiqueta(opcion), color = colores.textoPrincipal) },
                    onClick = {
                        abierto = false
                        onSeleccion(opcion)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun TarjetaResumenPreview() {
    VelokTheme {
        TarjetaResumen(
            metrica = MetricaProgreso.PESO,
            rango = RangoTiempo.TRES_DIAS,
            valores = listOf(2.0, 5.0, 3.0, 5.0, 4.0, 6.0, 5.5, 7.0, 8.0),
            explicacion = "",
            onMetrica = {},
            onRango = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
