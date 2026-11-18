package com.velok.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.velok.app.data.local.seguridad.HashContrasenas
import com.velok.app.data.repository.LocalCuentaRepository
import com.velok.app.data.repository.LocalEjercicioRepository
import com.velok.app.data.repository.LocalPlanRepository
import com.velok.app.data.repository.LocalSesionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Clock
import kotlin.time.Instant

// Utilidades de las pruebas de la base de datos local. Corren en la JVM de la computadora con
// Robolectric, que da el Context de Android que Room necesita.

/** Fecha fija para que el historial sembrado sea siempre el mismo. */
val relojFijo = object : Clock {
    override fun now(): Instant = Instant.parse("2026-07-29T12:00:00Z")
}

/** Pocas iteraciones: las pruebas no necesitan que el hash sea lento. */
val hashRapido = HashContrasenas(iteraciones = 1_000)

val contexto: Context get() = ApplicationProvider.getApplicationContext()

/** Base de datos en memoria: cada prueba empieza vacía. */
fun baseDatosEnMemoria(): BaseDatosVelok =
    Room.inMemoryDatabaseBuilder<BaseDatosVelok>(contexto).allowMainThreadQueries().build()

/** Base de datos en un archivo, para probar que los datos siguen ahí al volver a abrirla. */
fun baseDatosEnArchivo(nombre: String): BaseDatosVelok =
    Room.databaseBuilder<BaseDatosVelok>(contexto, contexto.getDatabasePath(nombre).absolutePath)
        .allowMainThreadQueries()
        .build()

/** Los repositorios locales sobre una base de datos, como los arma Koin en la app. */
class Repositorios(val baseDatos: BaseDatosVelok) {
    val sembrador = SembradorDatos(baseDatos, hashRapido, relojFijo)
    val ejercicios = LocalEjercicioRepository(baseDatos, sembrador)
    val planes = LocalPlanRepository(baseDatos, sembrador)
    val sesiones = LocalSesionRepository(baseDatos, sembrador)
    val cuentas = LocalCuentaRepository(baseDatos, sembrador, hashRapido)
}

/**
 * Junta lo que emite un Flow para revisar los cambios. Room emite desde sus propios hilos, por eso
 * se recolecta en Dispatchers.Default y [esperar] tiene un límite de tiempo real.
 */
class Emisiones<T>(scope: CoroutineScope, flow: Flow<T>) {
    private val canal = Channel<T>(Channel.UNLIMITED)
    val trabajo = scope.launch(Dispatchers.Default) { flow.collect { canal.send(it) } }

    /**
     * Espera la primera emisión que cumpla [condicion] (descarta las anteriores). Corre en
     * Dispatchers.Default para que el límite sea de tiempo real y no el tiempo virtual de runTest.
     */
    suspend fun esperar(condicion: (T) -> Boolean = { true }): T = withContext(Dispatchers.Default) {
        withTimeout(5_000) {
            var valor = canal.receive()
            while (!condicion(valor)) valor = canal.receive()
            valor
        }
    }
}
