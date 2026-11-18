package com.velok.app.presentation.formulario

import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.validacion.ErrorCampo
import kotlinx.datetime.LocalDate

/** Pasos en el orden de los puntos de los diseños. */
enum class PasoFormulario { NACIMIENTO, UNIDAD, ESTATURA, PESO, META }

data class FormularioUiState(
    val hoy: LocalDate,
    val nombre: String = "",
    val paso: PasoFormulario = PasoFormulario.NACIMIENTO,
    val fechaNacimiento: LocalDate,
    val unidad: UnidadPeso = UnidadPeso.KG,
    val estaturaTexto: String = "",
    /** En la unidad elegida (kg o lb). */
    val pesoTexto: String = "",
    val meta: MetaEntrenamiento = MetaEntrenamiento.PERDER_GRASA,
    /** Error del paso actual (se muestra bajo la pregunta). */
    val error: ErrorCampo? = null,
    val guardando: Boolean = false,
)
