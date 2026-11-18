package com.velok.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent

/**
 * Código específico de plataforma (expect/actual): el motor que hace las peticiones HTTP.
 * - Android (androidMain): OkHttp.
 * - iOS (iosMain): Darwin, que usa NSURLSession del sistema.
 * El resto de la configuración es común ([crearClienteHttp]).
 */
expect fun motorHttp(): HttpClientEngine

/**
 * Cliente HTTP de la app. No lanza excepciones por códigos de error (expectSuccess = false):
 * [ClienteExerciseDb] revisa el código de cada respuesta.
 */
fun crearClienteHttp(motor: HttpClientEngine = motorHttp()): HttpClient = HttpClient(motor) {
    expectSuccess = false
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 20_000
    }
    install(UserAgent) { agent = "Velok/1.0" }
}
