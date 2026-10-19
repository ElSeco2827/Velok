package com.velok.app.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.velok.app.domain.model.ComparacionMuscular
import com.velok.app.domain.model.GrupoMuscular
import com.velok.app.domain.model.HorasSemana
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.ResumenHoras
import com.velok.app.domain.model.Usuario
import com.velok.app.getPlatform
import com.velok.app.presentation.components.EstadoCargando
import com.velok.app.presentation.components.GraficaBarras
import com.velok.app.presentation.components.GraficaRadar
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatearNumero
import com.velok.app.presentation.util.formatoCorto
import com.velok.app.presentation.util.nombre
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ceil
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.esta_semana
import velok.shared.generated.resources.foto_perfil
import velok.shared.generated.resources.grafica_horas
import velok.shared.generated.resources.grafica_radar
import velok.shared.generated.resources.horas_eje
import velok.shared.generated.resources.horas_valor
import velok.shared.generated.resources.ic_flecha_abajo
import velok.shared.generated.resources.plataforma_actual
import velok.shared.generated.resources.saludo

/** Cada cuántas semanas se escribe una fecha en el eje X de las barras. */
private const val SEMANAS_ENTRE_ETIQUETAS = 2

/** Perfil (diseño "Pantalla progreso-1"): saludo, horas por semana y radar de grupos musculares. */
@Composable
fun PerfilScreen(viewModel: PerfilViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    // Ejemplo visible de expect/actual: el nombre viene de androidMain o iosMain.
    val plataforma = remember { getPlatform().name }
    PerfilContenido(estado, plataforma, viewModel::cambiarRango)
}

@Composable
fun PerfilContenido(
    estado: PerfilUiState,
    plataforma: String,
    onRango: (RangoTiempo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val usuario = estado.usuario
    if (estado.cargando || usuario == null) {
        EstadoCargando(modifier)
        return
    }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = usuario.fotoUrl,
                contentDescription = stringResource(Res.string.foto_perfil),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(colores.borde),
                error = ColorPainter(colores.borde),
                fallback = ColorPainter(colores.borde),
                modifier = Modifier.size(100.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(24.dp))
            Text(
                text = stringResource(Res.string.saludo, usuario.nombre),
                style = VelokTheme.tipografia.headlineMedium,
                color = colores.sobreFondo,
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().background(colores.fondoProfundo).padding(vertical = 16.dp)) {
            estado.horas?.let { horas ->
                EncabezadoHoras(horas.horasEstaSemana, estado.rango, onRango)
                GraficaHoras(horas, Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp))
            }
            Spacer(Modifier.height(24.dp))
            estado.musculos?.let { GraficaMusculos(it) }
            Text(
                text = stringResource(Res.string.plataforma_actual, plataforma),
                style = VelokTheme.tipografia.bodySmall,
                color = colores.sobreFondoSecundario,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun EncabezadoHoras(horasEstaSemana: Double, rango: RangoTiempo, onRango: (RangoTiempo) -> Unit) {
    val colores = VelokTheme.colores
    var abierto by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colores.sobreFondo)) {
                    append(stringResource(Res.string.horas_valor, formatearNumero(horasEstaSemana)))
                }
                append(" ")
                withStyle(SpanStyle(color = colores.sobreFondoSecundario)) { append(stringResource(Res.string.esta_semana)) }
            },
            style = VelokTheme.tipografia.titleMedium,
            modifier = Modifier.weight(1f),
        )
        Box {
            TextButton(onClick = { abierto = true }) {
                Text(stringResource(rango.nombre), color = colores.acento, style = VelokTheme.tipografia.bodyLarge)
                Spacer(Modifier.width(4.dp))
                Icon(painterResource(Res.drawable.ic_flecha_abajo), null, tint = colores.acento, modifier = Modifier.size(18.dp))
            }
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }, containerColor = colores.superficie) {
                PerfilViewModel.rangos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(stringResource(opcion.nombre), color = colores.textoPrincipal) },
                        onClick = {
                            abierto = false
                            onRango(opcion)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GraficaHoras(horas: ResumenHoras, modifier: Modifier = Modifier) {
    val maximo = ceil(horas.semanas.maxOfOrNull { it.horas } ?: 0.0).toInt().coerceAtLeast(1)
    val etiquetasY = (0..maximo).map { stringResource(Res.string.horas_eje, it.toString()) }
    val etiquetasX = horas.semanas.mapIndexed { i, semana ->
        if (i % SEMANAS_ENTRE_ETIQUETAS == 0) semana.inicioSemana.formatoCorto() else null
    }
    GraficaBarras(
        valores = horas.semanas.map { it.horas },
        etiquetasX = etiquetasX,
        etiquetasY = etiquetasY,
        modifier = modifier.semantics(mergeDescendants = true) {},
    )
    Text(
        text = stringResource(Res.string.grafica_horas),
        style = VelokTheme.tipografia.labelSmall,
        color = VelokTheme.colores.sobreFondoSecundario,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
private fun GraficaMusculos(comparacion: ComparacionMuscular) {
    val grupos = GrupoMuscular.entries
    val nombres = grupos.map { stringResource(it.nombre) }
    val actual = grupos.map { comparacion.actual[it] ?: 0 }
    val descripcion = stringResource(
        Res.string.grafica_radar,
        nombres.zip(actual).joinToString(", ") { (n, v) -> "$n $v" },
    )
    GraficaRadar(
        etiquetas = nombres,
        actual = actual.map { it.toDouble() },
        anterior = grupos.map { (comparacion.anterior[it] ?: 0).toDouble() },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .semantics { contentDescription = descripcion },
    )
}

@Preview
@Composable
private fun PerfilContenidoPreview() {
    val lunes = LocalDate(2026, 7, 27)
    VelokTheme {
        Surface(color = VelokTheme.colores.fondo) {
            PerfilContenido(
                estado = PerfilUiState(
                    cargando = false,
                    usuario = Usuario("Checo"),
                    horas = ResumenHoras(
                        horasEstaSemana = 3.0,
                        semanas = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 1.1, 1.5, 0.0, 1.8, 2.2, 2.7)
                            .mapIndexed { i, h -> HorasSemana(lunes.plus(i, DateTimeUnit.WEEK), h) },
                    ),
                    musculos = ComparacionMuscular(
                        actual = mapOf(GrupoMuscular.PIERNAS to 12, GrupoMuscular.BRAZOS to 5, GrupoMuscular.CORE to 3),
                        anterior = mapOf(GrupoMuscular.PIERNAS to 9, GrupoMuscular.BRAZOS to 2),
                    ),
                ),
                plataforma = "Android 35",
                onRango = {},
            )
        }
    }
}
