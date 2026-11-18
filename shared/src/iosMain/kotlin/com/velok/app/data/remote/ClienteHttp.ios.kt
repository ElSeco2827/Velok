package com.velok.app.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

// Implementación iOS de expect/actual: motor Darwin (NSURLSession del sistema).
actual fun motorHttp(): HttpClientEngine = Darwin.create()
