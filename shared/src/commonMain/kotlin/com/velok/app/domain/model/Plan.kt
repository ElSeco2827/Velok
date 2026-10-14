package com.velok.app.domain.model

data class Plan(
    val id: String,
    val categoria: TipoCategoria,
    val imagenUrl: String,
    val numeroEjercicios: Int,
)
