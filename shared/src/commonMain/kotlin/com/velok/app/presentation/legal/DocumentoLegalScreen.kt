package com.velok.app.presentation.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.components.BotonVelok
import com.velok.app.presentation.components.LogoHorizontal
import com.velok.app.presentation.theme.VelokTheme
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.continuar
import velok.shared.generated.resources.documento_privacidad
import velok.shared.generated.resources.documento_terminos

enum class DocumentoLegal(val parrafos: StringArrayResource) {
    TERMINOS(Res.array.documento_terminos),
    PRIVACIDAD(Res.array.documento_privacidad),
}

/**
 * "Términos y condiciones" y "Políticas de privacidad". Es contenido fijo, sin datos ni acciones
 * de negocio, por eso no tiene ViewModel. En el texto, "## " marca una sección, "### " un
 * subtítulo y "• " una viñeta.
 */
@Composable
fun DocumentoLegalScreen(
    documento: DocumentoLegal,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val parrafos = stringArrayResource(documento.parrafos)
    // Como en el diseño: el texto se desplaza y "Continuar" queda fijo abajo.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .systemBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(60.dp))
        LogoHorizontal(Modifier.align(Alignment.CenterHorizontally).width(266.dp))
        Spacer(Modifier.height(32.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            parrafos.forEachIndexed { i, parrafo -> Parrafo(i, parrafo) }
        }
        Spacer(Modifier.height(16.dp))
        BotonVelok(
            texto = stringResource(Res.string.continuar),
            onClick = onContinuar,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Parrafo(indice: Int, parrafo: String) {
    val colores = VelokTheme.colores
    when {
        indice == 0 -> Text(
            parrafo,
            style = VelokTheme.tipografia.titleMedium,
            color = colores.sobreFondo,
            modifier = Modifier.semantics { heading() },
        )
        parrafo.startsWith("### ") -> Text(
            parrafo.removePrefix("### "),
            style = VelokTheme.tipografia.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colores.sobreFondo,
            modifier = Modifier.padding(top = 6.dp),
        )
        parrafo.startsWith("## ") -> Text(
            parrafo.removePrefix("## "),
            style = VelokTheme.tipografia.titleSmall,
            color = colores.sobreFondo,
            modifier = Modifier.padding(top = 12.dp).semantics { heading() },
        )
        parrafo.startsWith("• ") -> Text(
            parrafo,
            style = VelokTheme.tipografia.bodyMedium,
            color = colores.sobreFondo,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp),
        )
        else -> Text(
            parrafo,
            style = VelokTheme.tipografia.bodyMedium,
            color = if (indice == 1) colores.sobreFondoSecundario else colores.sobreFondo,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview
@Composable
private fun DocumentoLegalPreview() {
    VelokTheme { DocumentoLegalScreen(DocumentoLegal.TERMINOS, onContinuar = {}) }
}
