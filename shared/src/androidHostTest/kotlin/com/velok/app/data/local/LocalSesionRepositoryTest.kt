package com.velok.app.data.local

import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.iniciarSesion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalSesionRepositoryTest {

    private val repos = Repositorios(baseDatosEnMemoria())
    private val sesiones = repos.sesiones

    @After
    fun cerrar() = repos.baseDatos.close()

    @Test
    fun laPrimeraVezTieneElHistorialDeEjemplo() = runTest {
        assertEquals(DatosMock.historial(relojFijo), sesiones.observarHistorial().first())
    }

    @Test
    fun guardaUnaSesionCompletaYElFlowLaEmite() = runTest {
        val emisiones = Emisiones(backgroundScope, sesiones.observarHistorial())
        val antes = emisiones.esperar()

        // Sesión con series "anterior" (las del plan), una completada y otra no.
        val inicio = relojFijo.now()
        val sesion = DatosMock.planes.first().iniciarSesion(id = "sesion-nueva", inicio = inicio).let { s ->
            s.copy(
                fin = inicio + 45.minutes,
                ejercicios = s.ejercicios.mapIndexed { i, ej ->
                    ej.copy(series = ej.series.mapIndexed { j, serie -> serie.copy(completada = (i + j) % 2 == 0) })
                },
            )
        }
        sesiones.guardarSesion(sesion)

        val despues = emisiones.esperar { it.size == antes.size + 1 }
        assertEquals(sesion, despues.last())
    }
}
