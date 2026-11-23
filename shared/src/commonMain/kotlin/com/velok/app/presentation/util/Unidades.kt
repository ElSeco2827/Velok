package com.velok.app.presentation.util

import androidx.compose.runtime.Composable
import com.velok.app.domain.model.MetricaProgreso
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.pesoEnUnidad
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.kg
import velok.shared.generated.resources.lb
import velok.shared.generated.resources.valor_peso
import velok.shared.generated.resources.valor_repeticiones
import kotlin.math.pow
import kotlin.math.roundToLong

// Regla de unidades: los pesos se guardan en kg y solo se convierten al mostrarlos o capturarlos
// (funciones del dominio pesoEnUnidad / pesoAKg). Aquí está lo que es solo de presentación.

/** Encabezado de la columna de peso en las tablas de series ("KG", "LB"). */
val UnidadPeso.columna: StringResource
    get() = when (this) {
        UnidadPeso.KG -> Res.string.kg
        UnidadPeso.LB -> Res.string.lb
    }

/**
 * Valor de una métrica de progreso ya convertido a la unidad del usuario, con su unidad
 * ("135 lb", "12,400 lb", "36 reps").
 */
@Composable
fun textoMetrica(valor: Double, metrica: MetricaProgreso, unidad: UnidadPeso): String = when (metrica) {
    MetricaProgreso.PESO -> stringResource(Res.string.valor_peso, formatearNumero(valor), stringResource(unidad.sufijo))
    MetricaProgreso.VOLUMEN -> stringResource(Res.string.valor_peso, formatearMiles(valor.roundToLong()), stringResource(unidad.sufijo))
    MetricaProgreso.REPETICIONES -> stringResource(Res.string.valor_repeticiones, formatearMiles(valor.roundToLong()))
}

/** Peso guardado en kg, en la unidad del usuario y con 1 decimal como máximo ("61.2", "135"). */
fun formatearPeso(kg: Double, unidad: UnidadPeso): String = formatearNumero(pesoEnUnidad(kg, unidad))

/**
 * Valor que muestra un campo de peso. Se redondea a 3 decimales solo para comparar con lo que el
 * usuario escribió: 2 lb guardadas como 0.907... kg vuelven como 2.0000000000000004, y sin este
 * redondeo el campo creería que el valor cambió y borraría el "2." mientras se escribe "2.5".
 */
fun pesoParaCampo(kg: Double, unidad: UnidadPeso): Double = redondear(pesoEnUnidad(kg, unidad), 3)

private fun redondear(valor: Double, decimales: Int): Double {
    val factor = 10.0.pow(decimales)
    return (valor * factor).roundToLong() / factor
}
