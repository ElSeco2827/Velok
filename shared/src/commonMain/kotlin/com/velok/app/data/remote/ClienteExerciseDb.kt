package com.velok.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

/** Por qué no se pudo obtener una página. */
enum class MotivoFallo {
    /** Sin internet, DNS, tiempo agotado... (la petición no llegó o no volvió). */
    SIN_CONEXION,

    /** El servidor respondió con un error (5xx, 404...). */
    SERVIDOR,

    /** 429: demasiadas peticiones. La descarga se detiene y sigue en el siguiente arranque. */
    LIMITE_DE_PETICIONES,

    /** La respuesta no tiene la forma esperada (JSON inválido, success = false...). */
    RESPUESTA_INVALIDA,
}

sealed interface ResultadoPagina {
    data class Exito(val respuesta: RespuestaEjerciciosDto) : ResultadoPagina
    data class Fallo(val motivo: MotivoFallo) : ResultadoPagina
}

/**
 * Acceso a la API gratuita de ExerciseDB V1. No pide clave. Nunca lanza excepciones por errores
 * de red o de formato: los devuelve como [ResultadoPagina.Fallo] para que la app no se cierre.
 */
class ClienteExerciseDb(
    private val http: HttpClient,
    private val urlBase: String = URL_BASE,
) {
    /** Una página del catálogo. [cursor] null pide la primera. */
    suspend fun pagina(cursor: String?): ResultadoPagina {
        val respuesta: HttpResponse = try {
            http.get("${urlBase}exercises") {
                parameter("limit", TAMANO_PAGINA)
                if (cursor != null) parameter("after", cursor)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ResultadoPagina.Fallo(MotivoFallo.SIN_CONEXION)
        }
        return when {
            respuesta.status == HttpStatusCode.TooManyRequests -> ResultadoPagina.Fallo(MotivoFallo.LIMITE_DE_PETICIONES)
            !respuesta.status.isSuccess() -> ResultadoPagina.Fallo(MotivoFallo.SERVIDOR)
            else -> leer(respuesta)
        }
    }

    private suspend fun leer(respuesta: HttpResponse): ResultadoPagina {
        val cuerpo = try {
            json.decodeFromString<RespuestaEjerciciosDto>(respuesta.bodyAsText())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ResultadoPagina.Fallo(MotivoFallo.RESPUESTA_INVALIDA)
        }
        return if (cuerpo.success) ResultadoPagina.Exito(cuerpo) else ResultadoPagina.Fallo(MotivoFallo.RESPUESTA_INVALIDA)
    }

    companion object {
        const val URL_BASE = "https://oss.exercisedb.dev/api/v1/"

        /** Máximo que devuelve la API por página (aunque se pidan más). */
        const val TAMANO_PAGINA = 25

        /** Campos nuevos que agregue la API no deben romper la lectura. */
        private val json = Json { ignoreUnknownKeys = true }
    }
}
