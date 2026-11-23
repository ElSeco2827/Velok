package com.velok.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.buscar_ejercicio
import velok.shared.generated.resources.ic_buscar
import velok.shared.generated.resources.ic_cerrar
import velok.shared.generated.resources.limpiar_busqueda

/**
 * Campo de búsqueda del catálogo (pestaña Ejercicio y hoja "Agregar ejercicio"). Sin estado: el
 * ViewModel guarda el texto y espera a que el usuario deje de escribir antes de buscar.
 */
@Composable
fun BuscadorEjercicios(
    texto: String,
    onTexto: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val foco = LocalFocusManager.current
    val estilo = VelokTheme.tipografia.bodyLarge.copy(color = colores.textoPrincipal)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colores.superficie, VelokTheme.formas.small)
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_buscar),
            contentDescription = null,
            tint = colores.textoSecundario,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (texto.isEmpty()) {
                Text(stringResource(Res.string.buscar_ejercicio), style = estilo.copy(color = colores.textoSecundario))
            }
            BasicTextField(
                value = texto,
                onValueChange = onTexto,
                singleLine = true,
                textStyle = estilo,
                cursorBrush = SolidColor(colores.acentoTexto),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (texto.isNotEmpty()) {
            IconButton(onClick = { onTexto("") }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cerrar),
                    contentDescription = stringResource(Res.string.limpiar_busqueda),
                    tint = colores.textoSecundario,
                    modifier = Modifier.size(20.dp),
                )
            }
        } else {
            Spacer(Modifier.width(12.dp))
        }
    }
}

@Preview
@Composable
private fun BuscadorEjerciciosPreview() {
    VelokTheme {
        BuscadorEjercicios(texto = "bench", onTexto = {}, modifier = Modifier.padding(16.dp))
    }
}
