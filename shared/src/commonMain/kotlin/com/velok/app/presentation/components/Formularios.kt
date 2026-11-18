package com.velok.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ic_ojo
import velok.shared.generated.resources.ic_ojo_tachado
import velok.shared.generated.resources.mostrar_contrasena
import velok.shared.generated.resources.ocultar_contrasena
import velok.shared.generated.resources.paso_actual

/**
 * Campo de los formularios de cuenta: etiqueta arriba y texto subrayado, como en los diseños.
 * Si [error] no es null se muestra en rojo debajo.
 */
@Composable
fun CampoCuenta(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    esContrasena: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    accionTeclado: ImeAction = ImeAction.Next,
) {
    val colores = VelokTheme.colores
    var visible by rememberSaveable { mutableStateOf(false) }
    val base = VelokTheme.tipografia.headlineSmall.copy(fontWeight = FontWeight.Normal)
    val estilo = base.copy(color = colores.sobreFondo, textDecoration = TextDecoration.Underline)

    Column(modifier.fillMaxWidth()) {
        Text(etiqueta, style = base, color = colores.sobreFondo)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = valor,
                onValueChange = onCambio,
                singleLine = true,
                textStyle = estilo,
                cursorBrush = SolidColor(colores.acento),
                visualTransformation = if (esContrasena && !visible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (esContrasena) KeyboardType.Password else teclado,
                    imeAction = accionTeclado,
                ),
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = etiqueta
                        if (error != null) error(error)
                    },
                decorationBox = { campo ->
                    Box {
                        if (valor.isEmpty()) {
                            Text(placeholder, style = estilo.copy(color = colores.sobreFondoSecundario))
                        }
                        campo()
                    }
                },
            )
            if (esContrasena) {
                Icon(
                    painter = painterResource(if (visible) Res.drawable.ic_ojo_tachado else Res.drawable.ic_ojo),
                    contentDescription = stringResource(if (visible) Res.string.ocultar_contrasena else Res.string.mostrar_contrasena),
                    tint = colores.sobreFondoSecundario,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button) { visible = !visible }
                        .padding(8.dp),
                )
            }
        }
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(error, style = VelokTheme.tipografia.bodySmall, color = colores.alerta)
        }
    }
}

/** Botón verde lima de los diseños. Con [cargando] muestra un indicador y no responde a toques. */
@Composable
fun BotonVelok(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
) {
    val colores = VelokTheme.colores
    Surface(
        onClick = { if (!cargando) onClick() },
        modifier = modifier.height(46.dp),
        shape = VelokTheme.formas.small,
        color = colores.acento,
    ) {
        Box(Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            if (cargando) {
                CircularProgressIndicator(color = colores.sobreAcento, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Text(texto, style = VelokTheme.tipografia.headlineSmall, color = colores.sobreAcento, textAlign = TextAlign.Center)
            }
        }
    }
}

/** Texto verde lima subrayado que funciona como enlace ("¿Olvidaste tu contraseña?"). */
@Composable
fun EnlaceTexto(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = VelokTheme.tipografia.bodySmall.copy(textDecoration = TextDecoration.Underline),
        color = VelokTheme.colores.acento,
        modifier = modifier
            .clip(VelokTheme.formas.extraSmall)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(6.dp),
    )
}

/** Párrafo con partes que son enlaces: [partes] alterna texto normal y enlaces (texto, acción). */
@Composable
fun TextoConEnlaces(partes: List<Pair<String, (() -> Unit)?>>, modifier: Modifier = Modifier) {
    val colores = VelokTheme.colores
    val estiloEnlace = TextLinkStyles(SpanStyle(color = colores.acento, textDecoration = TextDecoration.Underline))
    val texto = buildAnnotatedString {
        partes.forEachIndexed { i, (parte, accion) ->
            if (i > 0) append(" ")
            if (accion == null) {
                append(parte)
            } else {
                withLink(LinkAnnotation.Clickable(tag = parte, styles = estiloEnlace) { accion() }) { append(parte) }
            }
        }
    }
    Text(
        text = texto,
        style = VelokTheme.tipografia.bodySmall,
        color = colores.sobreFondo,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/** Puntos de avance del cuestionario: los pasos hechos y el actual en verde lima. */
@Composable
fun IndicadorPasos(total: Int, actual: Int, modifier: Modifier = Modifier) {
    val colores = VelokTheme.colores
    val descripcion = stringResource(Res.string.paso_actual, actual + 1, total)
    Row(
        modifier = modifier.semantics { contentDescription = descripcion },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(total) { i ->
            Box(
                Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (i <= actual) colores.acento else colores.borde),
            )
        }
    }
}

/** Opción seleccionable en forma de píldora: rellena si está elegida, solo borde si no. */
@Composable
fun OpcionPildora(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Surface(
        selected = seleccionada,
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = VelokTheme.formas.small,
        color = if (seleccionada) colores.acento else colores.fondo,
        border = BorderStroke(1.dp, colores.acento),
    ) {
        Box(Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                texto,
                style = VelokTheme.tipografia.titleMedium,
                color = if (seleccionada) colores.sobreAcento else colores.sobreFondo,
            )
        }
    }
}

/** Valor numérico subrayado con su unidad ("170 cm"), centrado. */
@Composable
fun CampoMedida(
    valor: String,
    onCambio: (String) -> Unit,
    sufijo: String,
    placeholder: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
) {
    val colores = VelokTheme.colores
    val estilo = VelokTheme.tipografia.bodyLarge.copy(
        color = colores.sobreFondo,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.End,
    )
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        BasicTextField(
            value = valor,
            onValueChange = { nuevo ->
                onCambio(nuevo.filter { it.isDigit() || (decimal && (it == '.' || it == ',')) }.take(5))
            },
            singleLine = true,
            textStyle = estilo,
            cursorBrush = SolidColor(colores.acento),
            keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
            modifier = Modifier
                .widthIn(min = 28.dp)
                .width(IntrinsicSize.Min)
                .semantics { contentDescription = descripcion },
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (valor.isEmpty()) Text(placeholder, style = estilo.copy(color = colores.sobreFondoSecundario))
                    campo()
                }
            },
        )
        Text(" $sufijo", style = estilo.copy(textAlign = TextAlign.Start))
    }
}

@Preview
@Composable
private fun FormulariosPreview() {
    VelokTheme {
        Column(
            Modifier.background(VelokTheme.colores.fondo).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CampoCuenta("Correo electrónico", "", {}, "ejemplo@gmail.com")
            CampoCuenta("Contraseña", "abc", {}, "Mínimo 9 caracteres", error = "La contraseña es muy corta", esContrasena = true)
            BotonVelok("Iniciar sesión", {})
            IndicadorPasos(total = 5, actual = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionPildora("Kg", seleccionada = true, onClick = {})
                OpcionPildora("lb", seleccionada = false, onClick = {})
            }
            CampoMedida("170", {}, "cm", "170", "Estatura")
        }
    }
}
