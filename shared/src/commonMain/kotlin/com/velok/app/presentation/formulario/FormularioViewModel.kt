package com.velok.app.presentation.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.PerfilFisico
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.librasAKg
import com.velok.app.domain.usecase.GuardarPerfilFisicoUseCase
import com.velok.app.domain.usecase.ObtenerUsuarioUseCase
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasPerfil
import com.velok.app.presentation.util.fechaAjustada
import com.velok.app.presentation.util.textoAPeso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Cuestionario inicial de 5 pasos. Cada "Siguiente" valida el paso con las reglas del dominio. */
class FormularioViewModel(
    obtenerUsuario: ObtenerUsuarioUseCase,
    private val guardarPerfil: GuardarPerfilFisicoUseCase,
    clock: Clock,
) : ViewModel() {

    private val _estado = MutableStateFlow(
        clock.todayIn(TimeZone.currentSystemDefault()).let { hoy ->
            FormularioUiState(hoy = hoy, fechaNacimiento = hoy.minus(EDAD_INICIAL, DateTimeUnit.YEAR))
        },
    )
    val estado: StateFlow<FormularioUiState> = _estado.asStateFlow()

    init {
        obtenerUsuario()
            .filterNotNull()
            .onEach { usuario -> _estado.update { it.copy(nombre = usuario.nombre) } }
            .launchIn(viewModelScope)
    }

    /** El día se ajusta si el mes elegido es más corto. */
    fun cambiarFecha(dia: Int, mes: Int, anio: Int) = _estado.update {
        it.copy(fechaNacimiento = fechaAjustada(anio, mes, dia), error = null)
    }

    fun cambiarUnidad(unidad: UnidadPeso) = _estado.update { it.copy(unidad = unidad, error = null) }
    fun cambiarEstatura(texto: String) = _estado.update { it.copy(estaturaTexto = texto, error = null) }
    fun cambiarPeso(texto: String) = _estado.update { it.copy(pesoTexto = texto, error = null) }
    fun cambiarMeta(meta: MetaEntrenamiento) = _estado.update { it.copy(meta = meta, error = null) }

    /** Vuelve al paso anterior; devuelve false si ya está en el primero. */
    fun atras(): Boolean {
        val actual = _estado.value.paso
        if (actual.ordinal == 0) return false
        _estado.update { it.copy(paso = PasoFormulario.entries[actual.ordinal - 1], error = null) }
        return true
    }

    fun siguiente(onTerminado: () -> Unit) {
        val actual = _estado.value
        if (actual.guardando) return
        val error = validarPaso(actual)
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }
        if (actual.paso != PasoFormulario.META) {
            _estado.update { it.copy(paso = PasoFormulario.entries[actual.paso.ordinal + 1]) }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val perfil = PerfilFisico(
                fechaNacimiento = actual.fechaNacimiento,
                unidadPeso = actual.unidad,
                estaturaCm = actual.estaturaTexto.toInt(),
                pesoKg = pesoEnKg(actual)!!,
                meta = actual.meta,
            )
            val errorAlGuardar = guardarPerfil(perfil)
            _estado.update { it.copy(guardando = false, error = errorAlGuardar) }
            if (errorAlGuardar == null) onTerminado()
        }
    }

    private fun validarPaso(e: FormularioUiState): ErrorCampo? = when (e.paso) {
        PasoFormulario.NACIMIENTO -> ReglasPerfil.validarFechaNacimiento(e.fechaNacimiento, e.hoy)
        PasoFormulario.UNIDAD -> null
        PasoFormulario.ESTATURA -> ReglasPerfil.validarEstatura(e.estaturaTexto.toIntOrNull())
        PasoFormulario.PESO -> ReglasPerfil.validarPeso(pesoEnKg(e))
        PasoFormulario.META -> null
    }

    /** El peso se escribe en la unidad elegida y se guarda siempre en kg. */
    private fun pesoEnKg(e: FormularioUiState): Double? {
        val valor = textoAPeso(e.pesoTexto) ?: return null
        return if (e.unidad == UnidadPeso.LB) librasAKg(valor) else valor
    }

    private companion object {
        /** Fecha que aparece seleccionada al abrir el paso. */
        const val EDAD_INICIAL = 20
    }
}
