package com.velok.app.presentation.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.SesionEntrenamiento
import com.velok.app.domain.model.agregarSerie
import com.velok.app.domain.model.alternarCompletada
import com.velok.app.domain.model.cambiarNota
import com.velok.app.domain.model.cambiarValores
import com.velok.app.domain.model.eliminarEjercicio
import com.velok.app.domain.usecase.IniciarSesionUseCase
import com.velok.app.domain.usecase.TerminarSesionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

/** Entrenamiento en curso. Las reglas de la sesión viven en el dominio (SesionEntrenamiento.kt). */
class SesionViewModel(
    planId: String,
    iniciarSesion: IniciarSesionUseCase,
    private val terminarSesion: TerminarSesionUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val _estado = MutableStateFlow(SesionUiState(ahora = clock.now()))
    val estado: StateFlow<SesionUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val sesion = iniciarSesion(planId)
            _estado.update { it.copy(cargando = false, sesion = sesion) }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(1.seconds)
                val ahora = clock.now()
                _estado.update { estado ->
                    estado.copy(ahora = ahora, descanso = estado.descanso?.takeIf { it.hasta > ahora })
                }
            }
        }
    }

    /** Al completar una serie empieza el descanso configurado para ese ejercicio. */
    fun alternarCompletada(indice: Int, idSerie: Int) {
        val antes = _estado.value.sesion ?: return
        val despues = antes.alternarCompletada(indice, idSerie)
        val seCompleto = despues.seriesCompletadas > antes.seriesCompletadas
        val segundos = despues.ejercicios.getOrNull(indice)?.descansoSegundos ?: 0
        _estado.update { estado ->
            estado.copy(
                sesion = despues,
                descanso = if (seCompleto && segundos > 0) {
                    DescansoActivo(indice, clock.now() + segundos.seconds)
                } else {
                    estado.descanso
                },
            )
        }
    }

    fun cambiarPeso(indice: Int, idSerie: Int, pesoKg: Double?) = editar { sesion ->
        val serie = sesion.ejercicios.getOrNull(indice)?.series?.firstOrNull { it.id == idSerie }
        sesion.cambiarValores(indice, idSerie, pesoKg, serie?.repeticiones)
    }

    fun cambiarRepeticiones(indice: Int, idSerie: Int, repeticiones: Int?) = editar { sesion ->
        val serie = sesion.ejercicios.getOrNull(indice)?.series?.firstOrNull { it.id == idSerie }
        sesion.cambiarValores(indice, idSerie, serie?.pesoKg, repeticiones)
    }

    fun agregarSerie(indice: Int) = editar { it.agregarSerie(indice) }
    fun cambiarNota(indice: Int, nota: String) = editar { it.cambiarNota(indice, nota) }

    fun eliminarEjercicio(indice: Int) {
        editar { it.eliminarEjercicio(indice) }
        // Los índices cambian: el descanso en curso ya no apunta al ejercicio correcto.
        _estado.update { it.copy(descanso = null) }
    }

    /** Guarda la sesión. Si no hay series completadas pide confirmación para salir sin guardar. */
    fun terminar(onTerminada: () -> Unit) {
        val sesion = _estado.value.sesion ?: return
        viewModelScope.launch {
            if (terminarSesion(sesion)) onTerminada() else _estado.update { it.copy(confirmarSalida = true) }
        }
    }

    fun cancelarSalida() = _estado.update { it.copy(confirmarSalida = false) }

    /** Termina el descanso antes de tiempo. */
    fun omitirDescanso() = _estado.update { it.copy(descanso = null) }

    private fun editar(cambio: (SesionEntrenamiento) -> SesionEntrenamiento) {
        _estado.update { estado -> estado.copy(sesion = estado.sesion?.let(cambio)) }
    }
}
