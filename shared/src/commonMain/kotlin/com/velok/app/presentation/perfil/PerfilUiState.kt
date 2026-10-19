package com.velok.app.presentation.perfil

import com.velok.app.domain.model.ComparacionMuscular
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.model.ResumenHoras
import com.velok.app.domain.model.Usuario

data class PerfilUiState(
    val cargando: Boolean = true,
    val usuario: Usuario? = null,
    val rango: RangoTiempo = RangoTiempo.TRES_MESES,
    val horas: ResumenHoras? = null,
    val musculos: ComparacionMuscular? = null,
)
