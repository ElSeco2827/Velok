package com.velok.app.data.local

import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.FiltroCatalogo
import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.OrigenEjercicio
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoEquipo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Búsqueda del catálogo hecha en SQLite (EjercicioDao.buscar), a través del repositorio local.
 * Usa los 21 ejercicios de ejemplo, que tienen nombres en español con acentos.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BusquedaCatalogoTest {

    private val repos = Repositorios(baseDatosEnMemoria())

    @After
    fun cerrar() = repos.cerrar()

    private suspend fun ids(filtro: FiltroCatalogo) = repos.ejercicios.observarEjercicios(filtro).first().map { it.id }

    @Test
    fun noDistingueMayusculasNiAcentos() = runTest {
        for (texto in listOf("biceps", "BÍCEPS", "Bíceps", "  curl de BI ")) {
            assertEquals(listOf("curl-biceps"), ids(FiltroCatalogo(texto = texto)), texto)
        }
        assertEquals(listOf("jalon-polea"), ids(FiltroCatalogo(texto = "JALON")))
        assertEquals(
            setOf("curl-piernas-acostado", "jalon-polea", "remo-maquina"),
            ids(FiltroCatalogo(texto = "maquina")).toSet(),
        )
    }

    @Test
    fun seCombinaConElEquipo() = runTest {
        assertEquals(
            setOf("press-banca-barra", "press-inclinado-barra", "press-banca-mancuernas", "press-militar"),
            ids(FiltroCatalogo(texto = "press")).toSet(),
        )
        assertEquals(listOf("press-banca-mancuernas"), ids(FiltroCatalogo(texto = "press", equipo = TipoEquipo.PESA)))
        assertEquals(emptyList(), ids(FiltroCatalogo(texto = "press", equipo = TipoEquipo.TAPETE)))
    }

    @Test
    fun encuentraPorMusculoOCategoriaEnEspanol() = runTest {
        // La presentación traduce "tríceps" al músculo TRICEPS; el nombre no tiene que coincidir.
        assertEquals(
            listOf("extension-triceps"),
            ids(FiltroCatalogo(texto = "triceps-no-en-el-nombre", musculosTexto = setOf(Musculo.TRICEPS))),
        )
        assertEquals(
            setOf("plancha", "crunch"),
            ids(FiltroCatalogo(texto = "abdomen", categoriasTexto = setOf(TipoCategoria.ABDOMEN))).toSet(),
        )
    }

    @Test
    fun losComodinesDeSqlSeBuscanLiteral() = runTest {
        assertEquals(emptyList(), ids(FiltroCatalogo(texto = "%")))
        assertEquals(emptyList(), ids(FiltroCatalogo(texto = "_")))
        assertEquals(emptyList(), ids(FiltroCatalogo(texto = "\\")))
    }

    @Test
    fun sinTextoDevuelveTodoElCatalogoVisible() = runTest {
        assertEquals(21, ids(FiltroCatalogo()).size)
        assertEquals(setOf("curl-biceps", "extension-triceps"), ids(FiltroCatalogo(categoria = TipoCategoria.BRAZOS)).toSet())
    }

    @Test
    fun conExerciseDbSoloBuscaEnElCatalogoDescargado() = runTest {
        repos.sembrador.asegurarDatos()
        val descargado = Ejercicio(
            id = "EIeI8Vf", nombre = "Barbell bench press", categoria = TipoCategoria.PECHO, equipo = TipoEquipo.BARRA,
            musculoPrimario = Musculo.PECHO, musculosSecundarios = listOf(Musculo.TRICEPS),
            imagenUrl = "https://static.exercisedb.dev/media/EIeI8Vf.gif", origen = OrigenEjercicio.EXERCISEDB,
        )
        repos.baseDatos.ejercicioDao().guardar(listOf(descargado.aEntidad(orden = 0)))

        assertEquals(listOf("EIeI8Vf"), ids(FiltroCatalogo(texto = "BENCH")))
        // Los de ejemplo ya no aparecen en la búsqueda, aunque coincidan.
        assertEquals(emptyList(), ids(FiltroCatalogo(texto = "press de banca")))
    }
}
