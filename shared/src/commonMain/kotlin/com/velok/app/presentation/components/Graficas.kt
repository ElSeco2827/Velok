package com.velok.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velok.app.presentation.theme.VelokTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Línea verde lima con área degradada, líneas guía horizontales y un punto en el último valor
 * (tarjeta "Resumen"). Necesita al menos 2 valores para dibujar la línea.
 */
@Composable
fun GraficaLinea(
    valores: List<Double>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    Canvas(modifier) {
        val guias = 6
        repeat(guias) { i ->
            val y = size.height * i / (guias - 1)
            drawLine(colores.borde, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
        if (valores.size < 2) return@Canvas

        val minimo = valores.min()
        val rango = (valores.max() - minimo).takeIf { it > 0 } ?: 1.0
        val margen = 8.dp.toPx()
        val alto = size.height - margen * 2
        val paso = size.width / (valores.size - 1)
        val puntos = valores.mapIndexed { i, v ->
            Offset(i * paso, margen + alto - ((v - minimo) / rango * alto).toFloat())
        }
        val linea = Path().apply {
            moveTo(puntos.first().x, puntos.first().y)
            puntos.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val area = Path().apply {
            addPath(linea)
            lineTo(puntos.last().x, size.height)
            lineTo(puntos.first().x, size.height)
            close()
        }
        drawPath(area, Brush.verticalGradient(listOf(colores.acento.copy(alpha = 0.25f), Color.Transparent)))
        drawPath(
            linea,
            colores.acento,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawCircle(colores.acento.copy(alpha = 0.3f), radius = 7.dp.toPx(), center = puntos.last())
        drawCircle(colores.acento, radius = 4.dp.toPx(), center = puntos.last())
    }
}

/**
 * Barras verticales con etiquetas en ambos ejes. [etiquetasY] tiene una etiqueta por cada valor
 * entero desde 0 (la última marca el tope del eje). En X solo se dibujan las etiquetas que no
 * son null, para no amontonarlas.
 */
@Composable
fun GraficaBarras(
    valores: List<Double>,
    etiquetasX: List<String?>,
    etiquetasY: List<String>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val medidor = rememberTextMeasurer()
    val estiloEje = VelokTheme.tipografia.labelSmall.copy(color = colores.sobreFondoSecundario)
    Canvas(modifier) {
        if (etiquetasY.size < 2) return@Canvas
        val maximoEntero = etiquetasY.size - 1
        val textosY = etiquetasY
        val anchoEje = textosY.maxOf { medidor.measure(it, estiloEje).size.width } + 8.dp.toPx()
        val altoEjeX = medidor.measure("0", estiloEje).size.height + 6.dp.toPx()
        val areaAlto = size.height - altoEjeX
        val areaAncho = size.width - anchoEje

        textosY.forEachIndexed { i, texto ->
            val y = areaAlto - areaAlto * i / maximoEntero
            drawLine(colores.bordeOscuro, Offset(anchoEje, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val medida = medidor.measure(texto, estiloEje).size
            drawText(medidor, texto, Offset(0f, (y - medida.height / 2f).coerceAtLeast(0f)), estiloEje)
        }
        if (valores.isEmpty()) return@Canvas

        val ranura = areaAncho / valores.size
        val anchoBarra = ranura * 0.55f
        valores.forEachIndexed { i, v ->
            val alto = (v / maximoEntero * areaAlto).toFloat()
            val x = anchoEje + ranura * i + (ranura - anchoBarra) / 2
            if (alto > 0f) {
                drawRect(colores.acento, Offset(x, areaAlto - alto), androidx.compose.ui.geometry.Size(anchoBarra, alto))
            }
            etiquetasX.getOrNull(i)?.let { texto ->
                val medida = medidor.measure(texto, estiloEje).size
                val xTexto = (x + anchoBarra / 2 - medida.width / 2).coerceIn(anchoEje, size.width - medida.width)
                drawText(medidor, texto, Offset(xTexto, areaAlto + 6.dp.toPx()), estiloEje)
            }
        }
    }
}

/**
 * Radar hexagonal: un polígono verde lima con el periodo actual y uno claro con el anterior.
 * [actual] y [anterior] van en el mismo orden que [etiquetas].
 */
@Composable
fun GraficaRadar(
    etiquetas: List<String>,
    actual: List<Double>,
    anterior: List<Double>,
    modifier: Modifier = Modifier,
) {
    val colores = VelokTheme.colores
    val medidor = rememberTextMeasurer()
    val estilo = VelokTheme.tipografia.bodySmall.copy(color = colores.sobreFondoSecundario)
    Canvas(modifier) {
        val lados = etiquetas.size
        if (lados < 3) return@Canvas
        val altoTexto = medidor.measure("A", estilo).size.height
        val radio = minOf(size.width / 2f - 72.dp.toPx(), size.height / 2f - altoTexto * 1.6f)
        val centro = Offset(size.width / 2f, size.height / 2f)
        fun vertice(i: Int, fraccion: Float): Offset {
            // Empieza arriba a la izquierda, como en el diseño (Espalda, Pecho, Core, ...).
            val angulo = -PI / 2 - PI / lados + 2 * PI * i / lados
            return Offset(
                centro.x + (radio * fraccion * cos(angulo)).toFloat(),
                centro.y + (radio * fraccion * sin(angulo)).toFloat(),
            )
        }
        fun poligono(fracciones: List<Float>) = Path().apply {
            fracciones.forEachIndexed { i, f ->
                val p = vertice(i, f)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }

        val anillos = 4
        (1..anillos).forEach { r ->
            drawPath(poligono(List(lados) { r / anillos.toFloat() }), colores.bordeOscuro, style = Stroke(1.dp.toPx()))
        }
        (0 until lados).forEach { i -> drawLine(colores.bordeOscuro, centro, vertice(i, 1f), strokeWidth = 1.dp.toPx()) }

        val maximo = (actual + anterior).maxOrNull()?.takeIf { it > 0 } ?: 1.0
        fun fracciones(valores: List<Double>) = valores.map { (it / maximo).toFloat().coerceIn(0f, 1f) }
        val previo = poligono(fracciones(anterior))
        drawPath(previo, colores.sobreFondo.copy(alpha = 0.35f))
        drawPath(previo, colores.sobreFondo.copy(alpha = 0.6f), style = Stroke(1.5.dp.toPx()))
        val presente = poligono(fracciones(actual))
        drawPath(presente, colores.acento.copy(alpha = 0.35f))
        drawPath(presente, colores.acento, style = Stroke(2.dp.toPx(), join = StrokeJoin.Round))

        etiquetas.forEachIndexed { i, texto ->
            val p = vertice(i, 1f)
            val medida = medidor.measure(texto, estilo).size
            val dx = p.x - centro.x
            val x = when {
                dx > radio * 0.3f -> p.x + 8.dp.toPx()
                dx < -radio * 0.3f -> p.x - medida.width - 8.dp.toPx()
                else -> p.x - medida.width / 2f
            }
            val y = if (p.y < centro.y) p.y - medida.height - 4.dp.toPx() else p.y + 4.dp.toPx()
            drawText(medidor, texto, Offset(x, y), estilo)
        }
    }
}

@Preview
@Composable
private fun GraficasPreview() {
    VelokTheme {
        androidx.compose.foundation.layout.Column(Modifier.background(VelokTheme.colores.fondoProfundo).padding(16.dp)) {
            GraficaLinea(listOf(2.0, 5.0, 3.0, 5.0, 4.0, 6.0, 5.5, 7.0, 8.0), Modifier.fillMaxWidth().height(140.dp))
            GraficaBarras(
                valores = listOf(0.0, 0.0, 2.0, 1.1, 1.5, 0.0, 1.8, 2.2, 2.7),
                etiquetasX = listOf("26 jul", null, null, "6 sep", null, null, "4 oct", null, null),
                etiquetasY = listOf("0 hrs", "1 hrs", "2 hrs", "3 hrs"),
                modifier = Modifier.fillMaxWidth().height(180.dp),
            )
            GraficaRadar(
                etiquetas = listOf("Espalda", "Pecho", "Core", "Hombros", "Brazos", "Piernas"),
                actual = listOf(2.0, 1.0, 3.0, 4.0, 5.0, 12.0),
                anterior = listOf(1.0, 1.0, 1.0, 2.0, 2.0, 9.0),
                modifier = Modifier.fillMaxWidth().height(280.dp),
            )
        }
    }
}
