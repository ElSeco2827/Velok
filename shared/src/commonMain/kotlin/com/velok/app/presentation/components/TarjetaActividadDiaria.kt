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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.domain.model.ActividadDiaria
import com.velok.app.domain.model.ActividadSalud
import com.velok.app.domain.model.Metrica
import com.velok.app.domain.model.TipoMetrica
import com.velok.app.presentation.salud.AccionesSalud
import com.velok.app.presentation.salud.EstadoTarjetaSalud
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatearMiles
import com.velok.app.presentation.util.formatoLargo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.actividad_diaria
import velok.shared.generated.resources.pasos_de_meta
import velok.shared.generated.resources.salud_abrir_ajustes
import velok.shared.generated.resources.salud_abrir_tienda
import velok.shared.generated.resources.salud_conectar
import velok.shared.generated.resources.salud_conectar_mensaje
import velok.shared.generated.resources.salud_error
import velok.shared.generated.resources.salud_instalar
import velok.shared.generated.resources.salud_intentar_de_nuevo
import velok.shared.generated.resources.salud_no_disponible
import velok.shared.generated.resources.salud_rechazado
import velok.shared.generated.resources.sin_dato

/**
 * Tarjeta "Actividad diaria" según el estado de los datos de salud: los datos reales, o un
 * mensaje con la acción que corresponde (conectar, abrir ajustes, instalar Health Connect). Nunca
 * muestra números de ejemplo.
 */
@Composable
fun TarjetaActividadSalud(
    estado: EstadoTarjetaSalud,
    acciones: AccionesSalud,
    modifier: Modifier = Modifier,
) {
    val actividad = estado.actividad
    when {
        actividad is ActividadSalud.Datos -> TarjetaActividadDiaria(actividad.actividad, actividad.metricas, modifier)
        estado.cargando -> TarjetaMensaje(modifier) {
            Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VelokTheme.colores.acentoTexto)
            }
        }
        estado.error -> TarjetaMensaje(modifier, stringResource(Res.string.salud_error)) {
            BotonVelok(stringResource(Res.string.salud_intentar_de_nuevo), acciones.onReintentar)
        }
        actividad == ActividadSalud.SinPermiso && estado.permisoRechazado -> TarjetaMensaje(modifier, stringResource(Res.string.salud_rechazado)) {
            acciones.onAbrirAjustes?.let { BotonVelok(stringResource(Res.string.salud_abrir_ajustes), it) }
            TextButton(onClick = acciones.onConectar) {
                Text(stringResource(Res.string.salud_intentar_de_nuevo), color = VelokTheme.colores.acentoTexto)
            }
        }
        actividad == ActividadSalud.SinPermiso -> TarjetaMensaje(modifier, stringResource(Res.string.salud_conectar_mensaje)) {
            BotonVelok(stringResource(Res.string.salud_conectar), acciones.onConectar)
        }
        actividad == ActividadSalud.RequiereInstalar -> TarjetaMensaje(modifier, stringResource(Res.string.salud_instalar)) {
            BotonVelok(stringResource(Res.string.salud_abrir_tienda), acciones.onAbrirTienda)
        }
        else -> TarjetaMensaje(modifier, stringResource(Res.string.salud_no_disponible)) {}
    }
}

/** Tarjeta "Actividad diaria" con datos: fecha, pasos de la meta, anillo y métricas 2x2. */
@Composable
fun TarjetaActividadDiaria(
    actividad: ActividadDiaria,
    metricas: List<Metrica>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    MarcoTarjeta(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Titulo()
                Text(
                    text = actividad.fecha.formatoLargo(),
                    style = VelokTheme.tipografia.bodySmall,
                    color = colores.textoSecundario,
                )
                // De dónde sale el anillo: pasos del día sobre la meta (META_PASOS_DIARIOS).
                Text(
                    text = stringResource(
                        Res.string.pasos_de_meta,
                        // Sin datos, "--" como en la métrica de pasos (no un 0 que parezca real).
                        actividad.pasos?.let { formatearMiles(it) } ?: stringResource(Res.string.sin_dato),
                        formatearMiles(actividad.metaPasos.toLong()),
                    ),
                    style = VelokTheme.tipografia.bodySmall,
                    color = colores.textoSecundario,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            AnilloProgreso(progreso = actividad.progreso, modifier = Modifier.size(82.dp), grosor = 16.dp)
        }
        Spacer(Modifier.height(16.dp))
        metricas.chunked(2).forEachIndexed { fila, par ->
            if (fila > 0) Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                par.forEach { metrica ->
                    MetricaCard(metrica = metrica, modifier = Modifier.weight(1f))
                }
                if (par.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MarcoTarjeta(modifier: Modifier, contenido: @Composable () -> Unit) {
    val colores = VelokTheme.colores
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = VelokTheme.formas.large,
        color = colores.superficie,
        border = BorderStroke(1.dp, colores.borde),
    ) {
        Column(Modifier.padding(16.dp)) { contenido() }
    }
}

@Composable
private fun Titulo() {
    Text(
        text = stringResource(Res.string.actividad_diaria),
        style = VelokTheme.tipografia.titleLarge,
        color = VelokTheme.colores.acentoTexto,
        modifier = Modifier.semantics { heading() },
    )
}

/** Tarjeta sin datos: título, explicación y botones. */
@Composable
private fun TarjetaMensaje(
    modifier: Modifier,
    mensaje: String? = null,
    botones: @Composable () -> Unit,
) {
    MarcoTarjeta(modifier) {
        Titulo()
        if (mensaje != null) {
            Text(
                text = mensaje,
                style = VelokTheme.tipografia.bodyMedium,
                color = VelokTheme.colores.textoPrincipal,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { botones() }
    }
}

@Preview
@Composable
private fun TarjetaActividadDiariaPreview() {
    VelokTheme {
        TarjetaActividadDiaria(
            actividad = ActividadDiaria(LocalDate(2026, 10, 12), 0.7f, pasos = 5_600),
            metricas = listOf(
                Metrica(TipoMetrica.CALORIAS, 160.0, listOf(2f, 3f, 2.5f, 5f, 4f, 7f)),
                Metrica(TipoMetrica.PASOS, 5_600.0, listOf(1f, 2f, 4f, 3f, 5f, 8f)),
                Metrica(TipoMetrica.DISTANCIA, 1.5, listOf(3f, 2f, 4f, 3.5f, 6f, 8f)),
                Metrica(TipoMetrica.RITMO_CARDIACO, null, emptyList()),
            ),
        )
    }
}

@Preview
@Composable
private fun TarjetaSinPermisoPreview() {
    VelokTheme {
        TarjetaActividadSalud(
            estado = EstadoTarjetaSalud(cargando = false, actividad = ActividadSalud.SinPermiso),
            acciones = AccionesSalud(),
        )
    }
}

@Preview
@Composable
private fun TarjetaRechazadoPreview() {
    VelokTheme {
        TarjetaActividadSalud(
            estado = EstadoTarjetaSalud(cargando = false, actividad = ActividadSalud.SinPermiso, permisoRechazado = true),
            acciones = AccionesSalud(onAbrirAjustes = {}),
        )
    }
}

@Preview
@Composable
private fun TarjetaInstalarPreview() {
    VelokTheme {
        TarjetaActividadSalud(
            estado = EstadoTarjetaSalud(cargando = false, actividad = ActividadSalud.RequiereInstalar),
            acciones = AccionesSalud(),
        )
    }
}
