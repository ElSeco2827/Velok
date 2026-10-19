package com.velok.app.presentation.editarplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.Ejercicio
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.actualizarSerie
import com.velok.app.domain.model.agregarEjercicio
import com.velok.app.domain.model.agregarSerie
import com.velok.app.domain.model.alternarTipoSerie
import com.velok.app.domain.model.cambiarDescanso
import com.velok.app.domain.model.cambiarNota
import com.velok.app.domain.model.eliminarEjercicio
import com.velok.app.domain.model.renombrar
import com.velok.app.domain.usecase.EliminarPlanUseCase
import com.velok.app.domain.usecase.GuardarPlanUseCase
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import com.velok.app.domain.usecase.ObtenerPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Editor del plan. No hay botón de "guardar": cada cambio se aplica con las reglas del dominio
 * (EdicionPlan.kt) y se guarda de inmediato.
 */
class EditarPlanViewModel(
    private val planId: String,
    private val obtenerPlan: ObtenerPlanUseCase,
    private val guardarPlan: GuardarPlanUseCase,
    private val eliminarPlan: EliminarPlanUseCase,
    private val obtenerEjercicios: ObtenerEjerciciosUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(EditarPlanUiState())
    val estado: StateFlow<EditarPlanUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // Se lee una sola vez: a partir de aquí la copia local es la que manda.
            val plan = obtenerPlan(planId).first()
            _estado.update { it.copy(cargando = false, plan = plan) }
            val catalogo = obtenerEjercicios()
            _estado.update { it.copy(catalogo = catalogo) }
        }
    }

    fun renombrar(nombre: String) = editar { it.renombrar(nombre) }
    fun agregarEjercicio(ejercicio: Ejercicio) = editar { it.agregarEjercicio(ejercicio) }
    fun eliminarEjercicio(indice: Int) = editar { it.eliminarEjercicio(indice) }
    fun agregarSerie(indice: Int) = editar { it.agregarSerie(indice) }
    fun alternarTipoSerie(indice: Int, idSerie: Int) = editar { it.alternarTipoSerie(indice, idSerie) }
    fun cambiarDescanso(indice: Int, segundos: Int) = editar { it.cambiarDescanso(indice, segundos) }
    fun cambiarNota(indice: Int, nota: String) = editar { it.cambiarNota(indice, nota) }

    fun cambiarPeso(indice: Int, idSerie: Int, pesoKg: Double?) = editar { plan ->
        val serie = plan.ejercicios.getOrNull(indice)?.series?.firstOrNull { it.id == idSerie }
        plan.actualizarSerie(indice, idSerie, pesoKg, serie?.repeticiones)
    }

    fun cambiarRepeticiones(indice: Int, idSerie: Int, repeticiones: Int?) = editar { plan ->
        val serie = plan.ejercicios.getOrNull(indice)?.series?.firstOrNull { it.id == idSerie }
        plan.actualizarSerie(indice, idSerie, serie?.pesoKg, repeticiones)
    }

    fun borrarPlan(onBorrado: () -> Unit) {
        viewModelScope.launch {
            eliminarPlan(planId)
            onBorrado()
        }
    }

    private fun editar(cambio: (Plan) -> Plan) {
        val actual = _estado.value.plan ?: return
        val nuevo = cambio(actual)
        _estado.update { it.copy(plan = nuevo) }
        viewModelScope.launch { guardarPlan(nuevo) }
    }
}
