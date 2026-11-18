package com.velok.app.data.remote

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.SincronizacionCatalogoEntidad
import com.velok.app.data.local.aEntidad
import com.velok.app.domain.model.EstadoCatalogo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

sealed interface ResultadoSincronizacion {
    /** Se descargó el catálogo hasta la última página. */
    data class Completa(val paginas: Int, val ejercicios: Int) : ResultadoSincronizacion

    /** La última descarga completa es reciente: no se pidió nada. */
    data object NoNecesaria : ResultadoSincronizacion

    /** Se detuvo; lo ya guardado se conserva y la próxima vez sigue desde el cursor guardado. */
    data class Fallo(val motivo: MotivoFallo) : ResultadoSincronizacion
}

/**
 * Descarga el catálogo de ExerciseDB a la base de datos local ("primero local": la app siempre lee
 * de Room; esto solo la llena y la actualiza en segundo plano).
 *
 * Cuidado con los límites del plan gratuito (estrictos y sin cifras publicadas):
 * - Páginas de 25 con [pausa] de 1 segundo entre una y otra (unas 60 peticiones en total).
 * - Solo vuelve a descargar si la última descarga completa tiene más de [vigencia] (7 días).
 * - Un error (sin conexión, servidor caído, 429, respuesta inválida) detiene la descarga sin
 *   reintentos: se guarda el cursor y se continúa desde ahí en el siguiente arranque.
 * - Cada página se guarda al llegar, así el catálogo crece aunque la descarga se interrumpa.
 */
class SincronizadorCatalogo(
    private val cliente: ClienteExerciseDb,
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
    private val clock: Clock,
    private val pausa: Duration = PAUSA_ENTRE_PAGINAS,
    private val vigencia: Duration = VIGENCIA,
) {
    private val ejercicios = baseDatos.ejercicioDao()
    private val avance = baseDatos.sincronizacionDao()
    private val candado = Mutex()
    private val iniciado = MutableStateFlow(false)
    private val descargando = MutableStateFlow(false)
    private val ultimoResultado = MutableStateFlow<ResultadoSincronizacion?>(null)

    /**
     * Estado para la UI. Si ya hay ejercicios descargados siempre es LISTO (aunque una
     * actualización falle): el aviso solo aparece cuando no hay ningún ejercicio descargado.
     */
    val estado: Flow<EstadoCatalogo> = combine(
        descargando,
        ultimoResultado,
        ejercicios.observarCantidadDescargados(),
    ) { enCurso, resultado, descargados ->
        when {
            descargados > 0 -> EstadoCatalogo.LISTO
            enCurso -> EstadoCatalogo.DESCARGANDO
            resultado is ResultadoSincronizacion.Fallo -> EstadoCatalogo.SIN_CONEXION
            else -> EstadoCatalogo.LISTO
        }
    }

    /**
     * Inicia la sincronización en segundo plano una sola vez por arranque de la app. Si falla,
     * no se reintenta hasta el siguiente arranque (sin peticiones en bucle).
     */
    fun iniciarUnaVez(alcance: CoroutineScope) {
        if (!iniciado.compareAndSet(expect = false, update = true)) return
        alcance.launch { sincronizar() }
    }

    /** Descarga lo que haga falta. Nunca lanza excepciones (salvo cancelación). */
    suspend fun sincronizar(): ResultadoSincronizacion = candado.withLock {
        descargando.value = true
        try {
            val resultado = try {
                // Primero los datos de ejemplo: la siembra solo corre con la tabla de ejercicios vacía.
                sembrador.asegurarDatos()
                descargar()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Error inesperado (por ejemplo, al guardar): se trata como cualquier otro fallo.
                ResultadoSincronizacion.Fallo(MotivoFallo.RESPUESTA_INVALIDA)
            }
            ultimoResultado.value = resultado
            resultado
        } finally {
            descargando.value = false
        }
    }

    private suspend fun descargar(): ResultadoSincronizacion {
        val guardado = avance.obtener()
        val ultimaDescarga = guardado?.ultimaDescargaMs
        val aMedias = guardado?.enProgreso == true
        val reciente = ultimaDescarga != null &&
            clock.now().toEpochMilliseconds() - ultimaDescarga < vigencia.inWholeMilliseconds
        if (!aMedias && reciente) return ResultadoSincronizacion.NoNecesaria

        // Una descarga interrumpida sigue donde se quedó; si no, empieza desde la primera página.
        var cursor = if (aMedias) guardado.cursorPendiente else null
        var guardados = 0
        for (pagina in 1..MAXIMO_PAGINAS) {
            avance.guardar(SincronizacionCatalogoEntidad(ultimaDescargaMs = ultimaDescarga, cursorPendiente = cursor, enProgreso = true))
            if (pagina > 1) delay(pausa)

            val respuesta = when (val r = cliente.pagina(cursor)) {
                is ResultadoPagina.Fallo -> return ResultadoSincronizacion.Fallo(r.motivo)
                is ResultadoPagina.Exito -> r.respuesta
            }
            val nuevos = respuesta.data.mapNotNull(MapeoExerciseDb::aDominio).map { it.aEntidad(orden = 0) }
            ejercicios.guardar(nuevos)
            guardados += nuevos.size

            val siguiente = respuesta.meta?.nextCursor
            if (respuesta.meta?.hasNextPage != true || siguiente == null) {
                avance.guardar(
                    SincronizacionCatalogoEntidad(
                        ultimaDescargaMs = clock.now().toEpochMilliseconds(),
                        cursorPendiente = null,
                        enProgreso = false,
                    ),
                )
                return ResultadoSincronizacion.Completa(paginas = pagina, ejercicios = guardados)
            }
            // Un cursor que no avanza haría un bucle infinito de peticiones.
            if (siguiente == cursor) return ResultadoSincronizacion.Fallo(MotivoFallo.RESPUESTA_INVALIDA)
            cursor = siguiente
        }
        return ResultadoSincronizacion.Fallo(MotivoFallo.RESPUESTA_INVALIDA)
    }

    companion object {
        val PAUSA_ENTRE_PAGINAS = 1.seconds
        val VIGENCIA = 7.days

        /** Tope de seguridad: el catálogo tiene 1,500 ejercicios (60 páginas). */
        const val MAXIMO_PAGINAS = 200
    }
}
