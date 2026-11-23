package com.velok.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import com.velok.app.data.local.seguridad.HashContrasenas
import com.velok.app.data.remote.ClienteExerciseDb
import com.velok.app.data.remote.SincronizadorCatalogo
import com.velok.app.data.remote.crearClienteHttp
import com.velok.app.data.repository.LocalCuentaRepository
import com.velok.app.data.repository.LocalEjercicioRepository
import com.velok.app.data.repository.LocalPlanRepository
import com.velok.app.data.repository.LocalSesionRepository
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Clock
import kotlin.time.Duration
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

/**
 * Igual que la app: con un SQLiteDriver y las migraciones manuales. La app usa
 * BundledSQLiteDriver (sus binarios son de Android y no corren en la JVM de la computadora); aquí
 * se usa AndroidSQLiteDriver sobre el SQLite de Robolectric. Con un driver, Room ejecuta las
 * migraciones por `migrate(SQLiteConnection)`, el mismo camino que en el teléfono.
 */
private fun RoomDatabase.Builder<BaseDatosVelok>.comoLaApp(): BaseDatosVelok =
    conMigraciones().setDriver(AndroidSQLiteDriver()).build()

/** Base de datos en memoria: cada prueba empieza vacía. */
fun baseDatosEnMemoria(): BaseDatosVelok =
    Room.inMemoryDatabaseBuilder<BaseDatosVelok>(contexto).comoLaApp()

/** Base de datos en un archivo, para probar que los datos siguen ahí al volver a abrirla. */
fun baseDatosEnArchivo(nombre: String): BaseDatosVelok =
    Room.databaseBuilder<BaseDatosVelok>(contexto, contexto.getDatabasePath(nombre).absolutePath).comoLaApp()

/** Reloj que las pruebas pueden adelantar (por ejemplo, 8 días para la vigencia del catálogo). */
class RelojAjustable(var ahora: Instant = relojFijo.now()) : Clock {
    override fun now(): Instant = ahora
}

/** Servidor simulado sin conexión: las pruebas que no son de la descarga nunca usan internet. */
val servidorSinConexion: MockEngine get() = MockEngine { throw RuntimeException("Sin conexión (prueba)") }

/**
 * Los repositorios locales sobre una base de datos, como los arma Koin en la app. La descarga del
 * catálogo usa [motor] (un MockEngine) y no hace pausas entre páginas.
 */
class Repositorios(
    val baseDatos: BaseDatosVelok,
    motor: HttpClientEngine = servidorSinConexion,
    val reloj: RelojAjustable = RelojAjustable(),
) {
    val alcance = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val sembrador = SembradorDatos(baseDatos, hashRapido, relojFijo)
    val sincronizador = SincronizadorCatalogo(
        cliente = ClienteExerciseDb(crearClienteHttp(motor)),
        baseDatos = baseDatos,
        sembrador = sembrador,
        clock = reloj,
        pausa = Duration.ZERO,
    )
    val ejercicios = LocalEjercicioRepository(baseDatos, sembrador, sincronizador, alcance)
    val planes = LocalPlanRepository(baseDatos, sembrador)
    val sesiones = LocalSesionRepository(baseDatos, sembrador)
    val cuentas = LocalCuentaRepository(baseDatos, sembrador, hashRapido)

    /** Detiene la descarga en segundo plano (si se inició) y cierra la base de datos. */
    fun cerrar() {
        alcance.cancel()
        baseDatos.close()
    }
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
