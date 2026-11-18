package com.velok.app.presentation.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.EstadoCatalogo
import com.velok.app.domain.model.TipoEquipo
import com.velok.app.domain.usecase.ObtenerCategoriasUseCase
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import com.velok.app.domain.usecase.ObtenerEstadoCatalogoUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Catálogo de ejercicios y categorías. Observa el catálogo: cuando termina de descargarse una
 * página de ExerciseDB, la lista se actualiza sola.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EjercicioViewModel(
    private val obtenerCategorias: ObtenerCategoriasUseCase,
    private val obtenerEjercicios: ObtenerEjerciciosUseCase,
    private val obtenerEstadoCatalogo: ObtenerEstadoCatalogoUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(EjercicioUiState())
    val estado: StateFlow<EjercicioUiState> = _estado.asStateFlow()

    private var observacion: Job? = null

    init {
        cargar()
    }

    fun seleccionarPestana(pestana: PestanaEjercicio) {
        _estado.update { it.copy(pestana = pestana) }
    }

    /** Tocar el chip seleccionado lo desactiva y se vuelven a mostrar todos los ejercicios. */
    fun alternarEquipo(equipo: TipoEquipo) {
        val nuevo = if (_estado.value.equipo == equipo) null else equipo
        _estado.update { it.copy(equipo = nuevo) }
    }

    /** Empieza (o reinicia, desde "Reintentar") la observación del catálogo. */
    fun cargar() {
        observacion?.cancel()
        _estado.update { it.copy(cargando = true, error = false) }
        observacion = viewModelScope.launch {
            launch {
                try {
                    val categorias = obtenerCategorias()
                    _estado.update { it.copy(categorias = categorias) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _estado.update { it.copy(cargando = false, error = true) }
                }
            }
            // Cada cambio de filtro cambia de consulta; cada cambio en el catálogo vuelve a emitir.
            _estado.map { it.equipo }
                .distinctUntilChanged()
                .flatMapLatest { equipo -> obtenerEjercicios(equipo = equipo) }
                .onEach { lista -> _estado.update { it.copy(cargando = false, ejercicios = lista) } }
                .catch { _estado.update { it.copy(cargando = false, error = true) } }
                .launchIn(this)
            obtenerEstadoCatalogo()
                .onEach { catalogo -> _estado.update { it.copy(sinConexion = catalogo == EstadoCatalogo.SIN_CONEXION) } }
                .catch { /* El aviso es opcional: si falla, no se muestra. */ }
                .launchIn(this)
        }
    }
}
