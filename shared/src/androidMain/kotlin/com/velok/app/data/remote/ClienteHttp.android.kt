package com.velok.app.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

// Implementación Android de expect/actual: motor OkHttp.
actual fun motorHttp(): HttpClientEngine = OkHttp.create()
