package com.velok.app.data.local

import androidx.room.Embedded
import androidx.room.Relation

// Resultados de consultas que traen un plan o una sesión con todo su contenido en una sola lectura.
// Room llena las listas con @Relation; el orden se corrige al convertir (por "posicion").

data class PlanCompleto(
    @Embedded val plan: PlanEntidad,
    @Relation(entity = PlanEjercicioEntidad::class, parentColumn = "id", entityColumn = "planId")
    val ejercicios: List<PlanEjercicioCompleto>,
)

data class PlanEjercicioCompleto(
    @Embedded val datos: PlanEjercicioEntidad,
    @Relation(parentColumn = "ejercicioId", entityColumn = "id")
    val ejercicio: EjercicioEntidad,
    @Relation(parentColumn = "id", entityColumn = "planEjercicioId")
    val series: List<SeriePlanEntidad>,
)

data class SesionCompleta(
    @Embedded val sesion: SesionEntidad,
    @Relation(entity = SesionEjercicioEntidad::class, parentColumn = "id", entityColumn = "sesionId")
    val ejercicios: List<SesionEjercicioCompleto>,
)

data class SesionEjercicioCompleto(
    @Embedded val datos: SesionEjercicioEntidad,
    @Relation(parentColumn = "ejercicioId", entityColumn = "id")
    val ejercicio: EjercicioEntidad,
    @Relation(parentColumn = "id", entityColumn = "sesionEjercicioId")
    val series: List<SerieSesionEntidad>,
)
