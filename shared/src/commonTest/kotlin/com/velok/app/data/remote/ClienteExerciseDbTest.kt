package com.velok.app.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Cliente de ExerciseDB con un servidor simulado (MockEngine): no usa internet. */
class ClienteExerciseDbTest {

    private val peticiones = mutableListOf<HttpRequestData>()

    private fun cliente(respuesta: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) =
        ClienteExerciseDb(crearClienteHttp(MockEngine { peticion -> peticiones += peticion; respuesta(peticion) }))

    private fun MockRequestHandleScope.json(texto: String, estado: HttpStatusCode = HttpStatusCode.OK) =
        respond(texto, estado, headersOf(HttpHeaders.ContentType, "application/json"))

    @Test
    fun pideLaPrimeraPaginaConLimite25YSinCursor() = runTest {
        val resultado = cliente { json(JsonExerciseDb.PAGINA_1) }.pagina(cursor = null)

        val exito = assertIs<ResultadoPagina.Exito>(resultado)
        assertEquals(2, exito.respuesta.data.size)
        val url = peticiones.single().url
        assertEquals("/api/v1/exercises", url.encodedPath)
        assertEquals("25", url.parameters["limit"])
        assertEquals(null, url.parameters["after"])
    }

    @Test
    fun mandaElCursorComoAfter() = runTest {
        cliente { json(JsonExerciseDb.PAGINA_2) }.pagina(cursor = "03lzqwk")
        assertEquals("03lzqwk", peticiones.single().url.parameters["after"])
    }

    @Test
    fun sinConexion() = runTest {
        val resultado = cliente { throw RuntimeException("Unable to resolve host oss.exercisedb.dev") }.pagina(null)
        assertEquals(ResultadoPagina.Fallo(MotivoFallo.SIN_CONEXION), resultado)
    }

    @Test
    fun servidorCaido() = runTest {
        val resultado = cliente { json("{}", HttpStatusCode.ServiceUnavailable) }.pagina(null)
        assertEquals(ResultadoPagina.Fallo(MotivoFallo.SERVIDOR), resultado)
    }

    @Test
    fun demasiadasPeticiones() = runTest {
        val resultado = cliente { json("{}", HttpStatusCode.TooManyRequests) }.pagina(null)
        assertEquals(ResultadoPagina.Fallo(MotivoFallo.LIMITE_DE_PETICIONES), resultado)
    }

    @Test
    fun respuestaQueNoEsJson() = runTest {
        val resultado = cliente { respond(JsonExerciseDb.NO_ES_JSON, HttpStatusCode.OK) }.pagina(null)
        assertEquals(ResultadoPagina.Fallo(MotivoFallo.RESPUESTA_INVALIDA), resultado)
    }

    @Test
    fun respuestaSinExito() = runTest {
        val resultado = cliente { json(JsonExerciseDb.SIN_EXITO) }.pagina(null)
        assertEquals(ResultadoPagina.Fallo(MotivoFallo.RESPUESTA_INVALIDA), resultado)
    }
}
