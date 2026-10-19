package com.velok.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Paleta base (medida sobre los diseños de docs/diseno): solo se usa dentro del paquete theme.
// Las pantallas usan VelokTheme.colores.
internal val VerdeLima = Color(0xFFD3FA6E)
internal val VerdeLimaOscuro = Color(0xFF9DC53A)
internal val VerdeLimaSuave = Color(0xFFF5FEDF)
internal val Oliva = Color(0xFF46502E)
internal val Carbon = Color(0xFF171819)
internal val NegroPuro = Color(0xFF000000)
internal val Grafito = Color(0xFF1C1C1E)
internal val GrafitoBorde = Color(0xFF3A3A3C)
internal val Negro = Color(0xFF141414)
internal val Blanco = Color(0xFFFFFFFF)
internal val GrisPizarraClaro = Color(0xFFF8FAFC)
internal val GrisChip = Color(0xFFF1F5F9)
internal val BordeSutil = Color(0xFFE2E8F0)
internal val GrisTexto = Color(0xFF94A3B8)
internal val GrisIcono = Color(0xFFA0AEC0)
internal val GrisSobreOscuro = Color(0xFF8E8E93)
internal val Azul = Color(0xFF018BFD)
internal val Ambar = Color(0xFFD6AC40)
internal val RojoAlerta = Color(0xFFFF4B4B)
internal val SombraSuave = Color(0x59000000)

/** Colores semánticos de Velok. */
@Immutable
data class VelokColores(
    /** Verde lima de marca (#D3FA6E): anillos, gráficas, indicadores y degradados. */
    val acento: Color,
    /** Variante más oscura del acento para texto e íconos sobre fondos claros (legibilidad). */
    val acentoTexto: Color,
    /** Fondo suave verde: ítem seleccionado de la barra inferior y botones flotantes. */
    val acentoSuave: Color,
    val encabezado: Color,
    val sobreEncabezado: Color,
    /** Fondo oscuro general de la app. */
    val fondo: Color,
    /** Texto e íconos sobre `fondo`. */
    val sobreFondo: Color,
    /** Texto de apoyo sobre fondos oscuros (etiquetas de tablas, subtítulos). */
    val sobreFondoSecundario: Color,
    /** Fondo negro de las pantallas de sesión, edición y detalle. */
    val fondoProfundo: Color,
    /** Tarjetas claras sobre el fondo oscuro. */
    val superficie: Color,
    /** Fondo de los cuadros de ícono dentro de tarjetas claras. */
    val superficieAlterna: Color,
    /** Botones, chips y etiquetas sobre fondos oscuros. */
    val superficieOscura: Color,
    /** Borde de los campos sobre fondos oscuros. */
    val bordeOscuro: Color,
    /** Borde de las tarjetas claras. */
    val borde: Color,
    /** Texto principal sobre superficies claras. */
    val textoPrincipal: Color,
    /** Texto secundario sobre superficies claras. */
    val textoSecundario: Color,
    val iconoInactivo: Color,
    val sobreAcento: Color,
    val sobreImagen: Color,
    /** Azul de los diseños de sesión y editor: nombres de ejercicio en tablas y duración. */
    val informativo: Color,
    /** Series de calentamiento ("W"). */
    val calentamiento: Color,
    /** Chip de filtro seleccionado (fondo) sobre fondo oscuro. */
    val chipActivo: Color,
    /** Chip de filtro sin seleccionar (fondo). */
    val chipInactivo: Color,
    val alerta: Color,
    /** Sombra para texto sobre fotos. */
    val sombraTexto: Color,
)

internal val VelokColoresOscuros = VelokColores(
    acento = VerdeLima,
    acentoTexto = VerdeLimaOscuro,
    acentoSuave = VerdeLimaSuave,
    encabezado = Carbon,
    sobreEncabezado = Blanco,
    fondo = Carbon,
    sobreFondo = Blanco,
    sobreFondoSecundario = GrisSobreOscuro,
    fondoProfundo = NegroPuro,
    superficie = Blanco,
    superficieAlterna = GrisPizarraClaro,
    superficieOscura = Grafito,
    bordeOscuro = GrafitoBorde,
    borde = BordeSutil,
    textoPrincipal = Negro,
    textoSecundario = GrisTexto,
    iconoInactivo = GrisIcono,
    sobreAcento = Negro,
    sobreImagen = Blanco,
    informativo = Azul,
    calentamiento = Ambar,
    chipActivo = Oliva,
    chipInactivo = GrisChip,
    alerta = RojoAlerta,
    sombraTexto = SombraSuave,
)

internal val LocalVelokColores = staticCompositionLocalOf { VelokColoresOscuros }
