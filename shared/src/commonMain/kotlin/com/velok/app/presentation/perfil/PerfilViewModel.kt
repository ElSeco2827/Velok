package com.velok.app.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velok.app.domain.model.RangoTiempo
import com.velok.app.domain.usecase.ObtenerComparacionMuscularUseCase
import com.velok.app.domain.usecase.ObtenerResumenHorasUseCase
import com.velok.app.domain.usecase.ObtenerUsuarioUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Perfil: saludo, horas por semana y series por grupo muscular. */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(
    obtenerUsuario: ObtenerUsuarioUseCase,
    obtenerResumenHoras: ObtenerResumenHorasUseCase,
    obtenerComparacionMuscular: ObtenerComparacionMuscularUseCase,
) : ViewModel() {

    private val _estado = MutableStateFlow(PerfilUiState())
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val usuario = obtenerUsuario()
            _estado.update { it.copy(cargando = false, usuario = usuario) }
        }
        _estado
            .map { it.rango }
            .distinctUntilChanged()
            .flatMapLatest { rango -> combine(obtenerResumenHoras(rango), obtenerComparacionMuscular(rango)) { h, m -> h to m } }
            .onEach { (horas, musculos) -> _estado.update { it.copy(horas = horas, musculos = musculos) } }
            .launchIn(viewModelScope)
    }

    fun cambiarRango(rango: RangoTiempo) = _estado.update { it.copy(rango = rango) }

    companion object {
        /** Periodos que ofrece el selector del perfil. */
        val rangos = listOf(RangoTiempo.MES, RangoTiempo.TRES_MESES)
    }
}
