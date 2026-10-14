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
import velok.shared.generated.resources.nav_calendario
import velok.shared.generated.resources.nav_ejercicio
import velok.shared.generated.resources.nav_inicio
import velok.shared.generated.resources.nav_perfil

// Rutas tipadas de Navigation Compose.
@Serializable data object RutaInicio
@Serializable data object RutaCalendario
@Serializable data object RutaEjercicio
@Serializable data object RutaActividad
@Serializable data object RutaPerfil

/** Destinos de la barra inferior, en el orden en que se muestran. */
enum class DestinoPrincipal(
    val ruta: Any,
    val etiqueta: StringResource,
    val icono: DrawableResource,
) {
    INICIO(RutaInicio, Res.string.nav_inicio, Res.drawable.ic_inicio),
    CALENDARIO(RutaCalendario, Res.string.nav_calendario, Res.drawable.ic_calendario),
    EJERCICIO(RutaEjercicio, Res.string.nav_ejercicio, Res.drawable.ic_ejercicio),
    ACTIVIDAD(RutaActividad, Res.string.nav_actividad, Res.drawable.ic_actividad),
    PERFIL(RutaPerfil, Res.string.nav_perfil, Res.drawable.ic_perfil),
}
