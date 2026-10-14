package com.velok.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Paleta base: solo se usa dentro del paquete theme. Las pantallas usan VelokTheme.colores.
internal val VerdeLima = Color(0xFFD3FA6E)
internal val VerdeLimaOscuro = Color(0xFF9DC53A)
internal val VerdeLimaSuave = Color(0xFFF0FCCB)
internal val Negro = Color(0xFF141414)
internal val Blanco = Color(0xFFFFFFFF)
internal val Crema = Color(0xFFF8F4EC)
internal val CremaTarjeta = Color(0xFFF4EFE6)
internal val BordeSutil = Color(0xFFECE6DA)
internal val GrisTexto = Color(0xFF9A9A9A)
internal val GrisIcono = Color(0xFFB0B0B0)
internal val RojoAlerta = Color(0xFFFF4B4B)
internal val SombraSuave = Color(0x59000000)

/** Colores semánticos de Velok. */
@Immutable
data class VelokColores(
    /** Verde lima de marca (#D3FA6E): anillos, gráficas, indicadores y degradados. */
    val acento: Color,
    /** Variante más oscura del acento para texto e íconos sobre fondos claros (legibilidad). */
    val acentoTexto: Color,
    /** Fondo suave del ítem seleccionado en la barra inferior. */
    val acentoSuave: Color,
    val encabezado: Color,
    val sobreEncabezado: Color,
    val fondo: Color,
    val superficie: Color,
    val superficieAlterna: Color,
    val borde: Color,
    val textoPrincipal: Color,
    val textoSecundario: Color,
    val iconoInactivo: Color,
    val sobreAcento: Color,
    val sobreImagen: Color,
    val alerta: Color,
    /** Sombra para texto sobre fotos. */
    val sombraTexto: Color,
)

internal val VelokColoresClaros = VelokColores(
    acento = VerdeLima,
    acentoTexto = VerdeLimaOscuro,
    acentoSuave = VerdeLimaSuave,
    encabezado = Negro,
    sobreEncabezado = Blanco,
    fondo = Crema,
    superficie = Blanco,
    superficieAlterna = CremaTarjeta,
    borde = BordeSutil,
    textoPrincipal = Negro,
    textoSecundario = GrisTexto,
    iconoInactivo = GrisIcono,
    sobreAcento = Negro,
    sobreImagen = Blanco,
    alerta = RojoAlerta,
    sombraTexto = SombraSuave,
)

internal val LocalVelokColores = staticCompositionLocalOf { VelokColoresClaros }
