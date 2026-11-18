package com.velok.app.data.remote

import com.velok.app.data.local.Emisiones
import com.velok.app.data.local.Repositorios
import com.velok.app.data.local.aDominio
import com.velok.app.data.local.baseDatosEnMemoria
import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.EstadoCatalogo
import com.velok.app.domain.model.OrigenEjercicio
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

/** Servidor simulado que anota el cursor (`after`) de cada petición. */
private class ServidorSimulado(
    private val responder: MockRequestHandleScope.(numero: Int, after: String?) -> HttpResponseData =
        { _, after -> json(JsonExerciseDb.paginaPara(after)) },
) {
    val cursores = mutableListOf<String?>()
    val motor = MockEngine { peticion ->
        val after = peticion.url.parameters["after"]
        cursores += after
        responder(cursores.size, after)
    }
}

private fun MockRequestHandleScope.json(texto: String, estado: HttpStatusCode = HttpStatusCode.OK) =
    respond(texto, estado, headersOf(HttpHeaders.ContentType, "application/json"))

/**
 * Descarga del catálogo con Room en memoria y un servidor simulado (sin internet). Se llama a
 * `sincronizar()` directamente para controlar cuántas veces se descarga.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SincronizadorCatalogoTest {

    private var repos: Repositorios? = null

    private fun repositorios(servidor: ServidorSimulado?) =
        (if (servidor == null) Repositorios(baseDatosEnMemoria()) else Repositorios(baseDatosEnMemoria(), servidor.motor))
            .also { repos = it }

    @After
    fun cerrar() {
        repos?.cerrar()
    }

    private suspend fun Repositorios.catalogo() = baseDatos.ejercicioDao().observarCatalogo().first().map { it.aDominio() }

    @Test
    fun descargaTodasLasPaginasYOcultaLosEjemplos() = runTest {
        val servidor = ServidorSimulado()
        val repos = repositorios(servidor)

        assertEquals(ResultadoSincronizacion.Completa(paginas = 3, ejercicios = 6), repos.sincronizador.sincronizar())
        assertEquals(listOf(null, "03lzqwk", "0dCyly0"), servidor.cursores)

        val catalogo = repos.catalogo()
        assertEquals(6, catalogo.size)
        assertTrue(catalogo.all { it.origen == OrigenEjercicio.EXERCISEDB })
        assertEquals(catalogo.map { it.nombre }.sortedBy { it.lowercase() }, catalogo.map { it.nombre })
        assertEquals(EstadoCatalogo.LISTO, repos.sincronizador.estado.first())

        // Los de ejemplo ya no se ven en el catálogo, pero los planes y el detalle siguen funcionando.
        val ejemplo = DatosMock.ejercicios.first()
        assertEquals(ejemplo, repos.ejercicios.obtenerEjercicio(ejemplo.id))
        assertEquals(DatosMock.planes.first(), repos.planes.observarPlan(DatosMock.planes.first().id).first())
    }

    @Test
    fun sinConexionSeQuedanLosEjemplosYSeAvisa() = runTest {
        val repos = repositorios(servidor = null)

        assertEquals(ResultadoSincronizacion.Fallo(MotivoFallo.SIN_CONEXION), repos.sincronizador.sincronizar())
        assertEquals(DatosMock.ejercicios, repos.catalogo())
        assertEquals(EstadoCatalogo.SIN_CONEXION, repos.sincronizador.estado.first())
    }

    @Test
    fun servidorCaido() = runTest {
        val repos = repositorios(ServidorSimulado { _, _ -> json("{}", HttpStatusCode.InternalServerError) })

        assertEquals(ResultadoSincronizacion.Fallo(MotivoFallo.SERVIDOR), repos.sincronizador.sincronizar())
        assertEquals(DatosMock.ejercicios, repos.catalogo())
        assertEquals(EstadoCatalogo.SIN_CONEXION, repos.sincronizador.estado.first())
    }

    @Test
    fun respuestaInvalida() = runTest {
        val repos = repositorios(ServidorSimulado { _, _ -> respond(JsonExerciseDb.NO_ES_JSON, HttpStatusCode.OK) })

        assertEquals(ResultadoSincronizacion.Fallo(MotivoFallo.RESPUESTA_INVALIDA), repos.sincronizador.sincronizar())
        assertEquals(DatosMock.ejercicios, repos.catalogo())
    }

    @Test
    fun con429SeDetieneYElSiguienteArranqueSigueDesdeElCursor() = runTest {
        var limitar = true
        val servidor = ServidorSimulado { numero, after ->
            if (limitar && numero == 2) json("{}", HttpStatusCode.TooManyRequests) else json(JsonExerciseDb.paginaPara(after))
        }
        val repos = repositorios(servidor)

        assertEquals(ResultadoSincronizacion.Fallo(MotivoFallo.LIMITE_DE_PETICIONES), repos.sincronizador.sincronizar())
        assertEquals(listOf(null, "03lzqwk"), servidor.cursores)
        // La primera página quedó guardada: ya hay catálogo descargado y no se muestra el aviso.
        assertEquals(2, repos.catalogo().size)
        assertEquals(EstadoCatalogo.LISTO, repos.sincronizador.estado.first())

        // "Siguiente arranque": otro sincronizador sobre la misma base de datos.
        limitar = false
        val siguiente = Repositorios(repos.baseDatos, servidor.motor)
        assertEquals(ResultadoSincronizacion.Completa(paginas = 2, ejercicios = 4), siguiente.sincronizador.sincronizar())
        assertEquals(listOf(null, "03lzqwk", "03lzqwk", "0dCyly0"), servidor.cursores)
        assertEquals(6, repos.catalogo().size)
    }

    @Test
    fun noVuelveADescargarAntesDeSieteDias() = runTest {
        val servidor = ServidorSimulado()
        val repos = repositorios(servidor)
        repos.sincronizador.sincronizar()

        assertEquals(ResultadoSincronizacion.NoNecesaria, repos.sincronizador.sincronizar())
        repos.reloj.ahora += 6.days
        assertEquals(ResultadoSincronizacion.NoNecesaria, repos.sincronizador.sincronizar())
        assertEquals(3, servidor.cursores.size)

        repos.reloj.ahora += 2.days
        assertEquals(ResultadoSincronizacion.Completa(paginas = 3, ejercicios = 6), repos.sincronizador.sincronizar())
        assertEquals(listOf(null, "03lzqwk", "0dCyly0", null, "03lzqwk", "0dCyly0"), servidor.cursores)
        assertEquals(6, repos.catalogo().size)
    }

    @Test
    fun actualizarNoRompeLosPlanesQueUsanEjerciciosDescargados() = runTest {
        val repos = repositorios(ServidorSimulado())
        repos.sincronizador.sincronizar()

        val descargado = repos.catalogo().first()
        val original = DatosMock.planes.first()
        val plan = original.copy(id = "plan-api", ejercicios = listOf(original.ejercicios.first().copy(ejercicio = descargado)))
        repos.planes.guardarPlan(plan)

        repos.reloj.ahora += 8.days
        assertTrue(repos.sincronizador.sincronizar() is ResultadoSincronizacion.Completa)
        assertEquals(plan, repos.planes.observarPlan("plan-api").first())
    }

    @Test
    fun unCursorQueNoAvanzaNoHacePeticionesEnBucle() = runTest {
        // Responde siempre la primera página, aunque se pida otra.
        val servidor = ServidorSimulado { _, _ -> json(JsonExerciseDb.PAGINA_1) }
        val repos = repositorios(servidor)

        assertEquals(ResultadoSincronizacion.Fallo(MotivoFallo.RESPUESTA_INVALIDA), repos.sincronizador.sincronizar())
        assertEquals(2, servidor.cursores.size)
    }

    @Test
    fun elRepositorioDescargaSoloYSeIntentaUnaVezPorArranque() = runTest {
        val servidor = ServidorSimulado()
        val repos = repositorios(servidor)

        // Leer el catálogo inicia la descarga en segundo plano; la lista se actualiza sola.
        val emisiones = Emisiones(backgroundScope, repos.ejercicios.observarEjercicios())
        emisiones.esperar { lista -> lista.size == 6 && lista.all { it.origen == OrigenEjercicio.EXERCISEDB } }

        // Volver a abrir el catálogo no inicia otra descarga.
        repos.ejercicios.observarEjercicios().first()
        repos.ejercicios.observarEstadoCatalogo().first()
        assertEquals(3, servidor.cursores.size)
    }
}
