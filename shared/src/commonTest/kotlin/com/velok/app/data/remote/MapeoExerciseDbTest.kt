package com.velok.app.data.remote

import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.OrigenEjercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MapeoExerciseDbTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun mapear(texto: String) = MapeoExerciseDb.aDominio(json.decodeFromString<EjercicioDto>(texto))

    @Test
    fun conviertePesoCorporalConNombreEnInglesYGif() {
        val ejercicio = mapear(JsonExerciseDb.PERRO_BOCA_ARRIBA)!!
        assertEquals("01qpYSe", ejercicio.id)
        assertEquals("Upward facing dog", ejercicio.nombre)
        assertEquals(TipoCategoria.ESPALDA, ejercicio.categoria)
        assertEquals(TipoEquipo.TAPETE, ejercicio.equipo)
        assertEquals(Musculo.LUMBARES, ejercicio.musculoPrimario)
        assertEquals(listOf(Musculo.HOMBROS, Musculo.PECHO), ejercicio.musculosSecundarios)
        assertEquals("https://static.exercisedb.dev/media/01qpYSe.gif", ejercicio.imagenUrl)
        assertEquals(OrigenEjercicio.EXERCISEDB, ejercicio.origen)
    }

    @Test
    fun asistidoVaATapeteYCintura() {
        val ejercicio = mapear(JsonExerciseDb.ELEVACION_RODILLAS)!!
        assertEquals(TipoCategoria.ABDOMEN, ejercicio.categoria)
        assertEquals(TipoEquipo.TAPETE, ejercicio.equipo)
        assertEquals(Musculo.ABDOMINALES, ejercicio.musculoPrimario)
        // hip flexors -> ABDOMINALES, igual que el principal: no se repite.
        assertEquals(emptyList(), ejercicio.musculosSecundarios)
    }

    @Test
    fun conDosEquiposUsaElPrimero() {
        val ejercicio = mapear(JsonExerciseDb.CURL_INVERSO)!!
        assertEquals(TipoEquipo.TAPETE, ejercicio.equipo)
        assertEquals(TipoCategoria.PIERNA, ejercicio.categoria)
        assertEquals(Musculo.ISQUIOTIBIALES, ejercicio.musculoPrimario)
        assertEquals(listOf(Musculo.GLUTEOS, Musculo.PANTORRILLAS), ejercicio.musculosSecundarios)
    }

    @Test
    fun barraYMancuernas() {
        assertEquals(TipoEquipo.BARRA, mapear(JsonExerciseDb.PRESS_BRADFORD)!!.equipo)
        val curl = mapear(JsonExerciseDb.CURL_INVERTIDO)!!
        assertEquals(TipoEquipo.PESA, curl.equipo)
        assertEquals(TipoCategoria.BRAZOS, curl.categoria)
        assertEquals(Musculo.BICEPS, curl.musculoPrimario)
        assertEquals(listOf(Musculo.ANTEBRAZOS), curl.musculosSecundarios)
    }

    @Test
    fun cardioSinMusculoEquivalenteUsaElDeSuParteDelCuerpo() {
        val burpee = mapear(JsonExerciseDb.BURPEE)!!
        assertEquals(TipoCategoria.CARDIO, burpee.categoria)
        assertEquals(Musculo.CUADRICEPS, burpee.musculoPrimario)
        assertEquals(
            listOf(Musculo.ISQUIOTIBIALES, Musculo.PANTORRILLAS, Musculo.HOMBROS, Musculo.TRICEPS, Musculo.ABDOMINALES),
            burpee.musculosSecundarios,
        )
    }

    @Test
    fun valoresDesconocidosNoImpidenGuardarlo() {
        val ejercicio = mapear(JsonExerciseDb.VALORES_DESCONOCIDOS)!!
        assertEquals(TipoCategoria.FUERZA, ejercicio.categoria)
        assertEquals(TipoEquipo.OTRO, ejercicio.equipo)
        assertEquals(Musculo.ABDOMINALES, ejercicio.musculoPrimario)
        assertEquals(listOf(Musculo.GLUTEOS), ejercicio.musculosSecundarios)
    }

    @Test
    fun accesoriosVanAOtro() {
        assertEquals(TipoEquipo.OTRO, MapeoExerciseDb.equipo(listOf("resistance band")))
        assertEquals(TipoEquipo.OTRO, MapeoExerciseDb.equipo(listOf("medicine ball")))
        assertEquals(TipoEquipo.OTRO, MapeoExerciseDb.equipo(emptyList()))
        assertEquals(TipoEquipo.BARRA, MapeoExerciseDb.equipo(listOf("EZ bar")))
        assertEquals(TipoEquipo.MAQUINA, MapeoExerciseDb.equipo(listOf("leverage machine")))
    }

    @Test
    fun sinIdONombreSeDescarta() {
        assertNull(MapeoExerciseDb.aDominio(EjercicioDto(exerciseId = "", name = "push up")))
        assertNull(MapeoExerciseDb.aDominio(EjercicioDto(exerciseId = "x1", name = "  ")))
    }

    @Test
    fun leeUnaPaginaCompleta() {
        val pagina = json.decodeFromString<RespuestaEjerciciosDto>(JsonExerciseDb.PAGINA_1)
        assertEquals(true, pagina.success)
        assertEquals("03lzqwk", pagina.meta?.nextCursor)
        assertEquals(listOf("01qpYSe", "03lzqwk"), pagina.data.map { it.exerciseId })
    }
}
