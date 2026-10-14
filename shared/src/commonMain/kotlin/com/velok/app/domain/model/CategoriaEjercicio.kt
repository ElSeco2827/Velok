package com.velok.app.domain.model

enum class TipoCategoria {
    PECHO,
    FUERZA,
    PIERNA,
    ESPALDA,
    BRAZOS,
    ABDOMEN,
    HOMBROS,
    CARDIO,
}

data class CategoriaEjercicio(
    val id: String,
    val tipo: TipoCategoria,
    val imagenUrl: String,
)
