package com.velok.app.presentation.navigation

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.ic_actividad
import velok.shared.generated.resources.ic_calendario
import velok.shared.generated.resources.ic_ejercicio
import velok.shared.generated.resources.ic_inicio
import velok.shared.generated.resources.ic_perfil
import velok.shared.generated.resources.nav_actividad
import velok.shared.generated.resources.nav_ejercicio
import velok.shared.generated.resources.nav_inicio
import velok.shared.generated.resources.nav_perfil
import velok.shared.generated.resources.nav_planes

// Rutas tipadas de Navigation Compose.

// Raíces de cada pestaña de la barra inferior.
@Serializable data object RutaInicio
@Serializable data object RutaPlanes
@Serializable data object RutaEjercicio
@Serializable data object RutaActividad
@Serializable data object RutaPerfil

// Pantallas de detalle (se apilan sobre la pestaña desde la que se abren).
@Serializable data class RutaDetallePlan(val planId: String)
@Serializable data class RutaEditarPlan(val planId: String)
@Serializable data class RutaSesion(val planId: String)
@Serializable data class RutaDetalleEjercicio(val ejercicioId: String)

/** [categoria] es el nombre de un `TipoCategoria`. */
@Serializable data class RutaCategoria(val categoria: String)

/** Destinos de la barra inferior, en el orden en que se muestran. */
enum class DestinoPrincipal(
    val ruta: Any,
    val etiqueta: StringResource,
    val icono: DrawableResource,
) {
    INICIO(RutaInicio, Res.string.nav_inicio, Res.drawable.ic_inicio),
    PLANES(RutaPlanes, Res.string.nav_planes, Res.drawable.ic_calendario),
    EJERCICIO(RutaEjercicio, Res.string.nav_ejercicio, Res.drawable.ic_ejercicio),
    ACTIVIDAD(RutaActividad, Res.string.nav_actividad, Res.drawable.ic_actividad),
    PERFIL(RutaPerfil, Res.string.nav_perfil, Res.drawable.ic_perfil),
}
