package com.velok.app.presentation.carga

import com.velok.app.domain.model.DestinoInicial

/** [destino] es null mientras se decide a dónde ir. */
data class CargaUiState(val destino: DestinoInicial? = null)
