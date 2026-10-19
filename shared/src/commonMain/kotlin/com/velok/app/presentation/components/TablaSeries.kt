package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velok.app.domain.model.TipoSerie
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.formatearNumero
import com.velok.app.presentation.util.textoAPeso
import com.velok.app.presentation.util.textoARepeticiones
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.agregar_serie
import velok.shared.generated.resources.ic_check
import velok.shared.generated.resources.ic_mas
import velok.shared.generated.resources.ic_mas_opciones
import velok.shared.generated.resources.mas_opciones
import velok.shared.generated.resources.nota_placeholder
import velok.shared.generated.resources.serie_calentamiento
import velok.shared.generated.resources.sin_valor

/** Opción del menú ⋮ de un ejercicio. */
data class OpcionMenu(val texto: String, val onClick: () -> Unit)

/** Encabezado de un ejercicio en una tabla de series: foto redonda, nombre en azul y menú ⋮. */
@Composable
fun EncabezadoEjercicio(
    nombre: String,
    imagenUrl: String,
    onNombreClick: () -> Unit,
    opciones: List<OpcionMenu>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    var menuAbierto by remember { mutableStateOf(false) }
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = imagenUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(colores.superficie),
            error = ColorPainter(colores.superficie),
            modifier = Modifier.size(44.dp).clip(CircleShape).background(colores.superficie),
        )
        Text(
            text = nombre,
            style = VelokTheme.tipografia.titleMedium,
            color = colores.informativo,
            modifier = Modifier
                .weight(1f)
                .clip(VelokTheme.formas.extraSmall)
                .clickableConRol(onNombreClick)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        )
        Box {
            IconButton(onClick = { menuAbierto = true }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_mas_opciones),
                    contentDescription = stringResource(Res.string.mas_opciones),
                    tint = colores.sobreFondo,
                )
            }
            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false },
                containerColor = colores.superficie,
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.texto, color = colores.textoPrincipal) },
                        onClick = {
                            menuAbierto = false
                            opcion.onClick()
                        },
                    )
                }
            }
        }
    }
}

/** Encabezado de columna (SERIE, KG, REPS...). */
@Composable
fun EtiquetaColumna(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = VelokTheme.tipografia.labelMedium,
        color = VelokTheme.colores.sobreFondoSecundario,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/** Cuadro con el número de la serie, o "W" en ámbar si es de calentamiento. */
@Composable
fun InsigniaSerie(
    numero: Int,
    tipo: TipoSerie,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    descripcion: String? = null,
) {
    val colores = VelokTheme.colores
    val calentamiento = tipo == TipoSerie.CALENTAMIENTO
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(VelokTheme.formas.extraSmall)
            .background(colores.superficieOscura)
            .then(if (onClick != null) Modifier.clickableConRol(onClick) else Modifier)
            .then(if (descripcion != null) Modifier.semantics { contentDescription = descripcion } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (calentamiento) stringResource(Res.string.serie_calentamiento) else numero.toString(),
            style = VelokTheme.tipografia.titleSmall,
            color = if (calentamiento) colores.calentamiento else colores.sobreFondo,
        )
    }
}

/** Campo oscuro para el peso en kg. Vacío se muestra como "-". */
@Composable
fun CampoPeso(valor: Double?, onCambio: (Double?) -> Unit, modifier: Modifier = Modifier) {
    CampoNumero(
        valor = valor,
        formatear = { formatearNumero(it) },
        convertir = ::textoAPeso,
        teclado = KeyboardType.Decimal,
        filtro = { c -> c.isDigit() || c == '.' || c == ',' },
        onCambio = onCambio,
        modifier = modifier,
    )
}

/** Campo oscuro para las repeticiones. */
@Composable
fun CampoRepeticiones(valor: Int?, onCambio: (Int?) -> Unit, modifier: Modifier = Modifier) {
    CampoNumero(
        valor = valor,
        formatear = { it.toString() },
        convertir = ::textoARepeticiones,
        teclado = KeyboardType.Number,
        filtro = { c -> c.isDigit() },
        onCambio = onCambio,
        modifier = modifier,
    )
}

/**
 * El campo guarda su propio texto para que se pueda escribir libremente (por ejemplo "2." antes
 * de "2.5"); solo lo reemplaza si el valor externo cambia a algo distinto de lo escrito.
 */
@Composable
private fun <T : Any> CampoNumero(
    valor: T?,
    formatear: (T) -> String,
    convertir: (String) -> T?,
    teclado: KeyboardType,
    filtro: (Char) -> Boolean,
    onCambio: (T?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    var texto by remember { mutableStateOf(valor?.let(formatear).orEmpty()) }
    LaunchedEffect(valor) {
        if (convertir(texto) != valor) texto = valor?.let(formatear).orEmpty()
    }
    val estilo = VelokTheme.tipografia.bodyLarge.copy(color = colores.sobreFondo, textAlign = TextAlign.Center)
    BasicTextField(
        value = texto,
        onValueChange = { nuevo ->
            val limpio = nuevo.filter(filtro).take(6)
            texto = limpio
            onCambio(convertir(limpio))
        },
        singleLine = true,
        textStyle = estilo,
        cursorBrush = SolidColor(colores.acento),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier
            .height(38.dp)
            .border(1.dp, colores.bordeOscuro, VelokTheme.formas.extraSmall),
        decorationBox = { campo ->
            Box(Modifier.padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                if (texto.isEmpty()) {
                    Text(
                        stringResource(Res.string.sin_valor),
                        style = estilo.copy(color = colores.sobreFondoSecundario),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                campo()
            }
        },
    )
}

/** Botón de palomita para completar una serie: verde lima cuando está completada. */
@Composable
fun BotonCompletarSerie(
    completada: Boolean,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        selected = completada,
        onClick = onClick,
        modifier = modifier.size(38.dp).semantics { contentDescription = descripcion },
        shape = VelokTheme.formas.extraSmall,
        color = if (completada) colores.acento else colores.superficieOscura,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_check),
            contentDescription = null,
            tint = if (completada) colores.sobreAcento else colores.sobreFondoSecundario,
            modifier = Modifier.padding(9.dp),
        )
    }
}

/** Botón ancho "+ Agregar serie". */
@Composable
fun BotonAgregarSerie(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BotonOscuroAncho(
        texto = stringResource(Res.string.agregar_serie),
        onClick = onClick,
        modifier = modifier,
    )
}

/** Botón ancho oscuro con "+" (agregar serie, agregar ejercicio). */
@Composable
fun BotonOscuroAncho(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colores = VelokTheme.colores
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(44.dp),
        shape = VelokTheme.formas.small,
        color = colores.superficieOscura,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(Res.drawable.ic_mas), null, tint = colores.sobreFondo, modifier = Modifier.size(20.dp))
            Text(texto, style = VelokTheme.tipografia.bodyLarge, color = colores.sobreFondo)
        }
    }
}

/** Campo de nota de un ejercicio (se muestra al tocar "Nota"). */
@Composable
fun CampoNota(nota: String, onCambio: (String) -> Unit, modifier: Modifier = Modifier) {
    val colores = VelokTheme.colores
    BasicTextField(
        value = nota,
        onValueChange = onCambio,
        textStyle = VelokTheme.tipografia.bodyMedium.copy(color = colores.sobreFondo),
        cursorBrush = SolidColor(colores.acento),
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, colores.bordeOscuro), VelokTheme.formas.extraSmall)
            .padding(10.dp),
        decorationBox = { campo ->
            Box {
                if (nota.isEmpty()) {
                    Text(
                        stringResource(Res.string.nota_placeholder),
                        style = VelokTheme.tipografia.bodyMedium,
                        color = colores.sobreFondoSecundario,
                    )
                }
                campo()
            }
        },
    )
}

/** `clickable` con rol de botón, para lectores de pantalla. */
private fun Modifier.clickableConRol(onClick: () -> Unit): Modifier = clickable(role = Role.Button, onClick = onClick)

@Preview
@Composable
private fun TablaSeriesPreview() {
    VelokTheme {
        Column(
            Modifier.background(VelokTheme.colores.fondoProfundo).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EncabezadoEjercicio("Sentadilla (Barra)", "", {}, emptyList())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                InsigniaSerie(1, TipoSerie.NORMAL)
                CampoPeso(10.0, {}, Modifier.weight(1f))
                CampoRepeticiones(12, {}, Modifier.weight(1f))
                BotonCompletarSerie(completada = true, descripcion = "", onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                InsigniaSerie(0, TipoSerie.CALENTAMIENTO)
                CampoPeso(null, {}, Modifier.weight(1f))
                CampoRepeticiones(10, {}, Modifier.weight(1f))
                BotonCompletarSerie(completada = false, descripcion = "", onClick = {})
            }
            BotonAgregarSerie({})
        }
    }
}

/** Número que se muestra en cada serie: las normales se numeran 1, 2, 3...; el calentamiento lleva 0 ("W"). */
fun numerosDeSerie(tipos: List<TipoSerie>): List<Int> {
    var contador = 0
    return tipos.map { if (it == TipoSerie.NORMAL) ++contador else 0 }
}
