package com.velok.app.presentation.util

import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory

/**
 * Código específico de plataforma (expect/actual): soporte de GIF animados.
 * - Android: decodificadores de coil-gif (los GIF de ExerciseDB se animan).
 * - iOS: no hace nada. coil-gif solo se publica para Android (3.6.3), así que en iOS Coil decodifica
 *   el GIF con Skia y muestra solo su primer cuadro: la imagen se ve fija. Es una limitación de
 *   Coil, no de la app.
 */
expect fun ComponentRegistry.Builder.agregarDecodificadorGif()

/**
 * Cargador de imágenes de toda la app (Coil). Descarga con Ktor (mismo motor que la API, elegido
 * por plataforma) y guarda las imágenes en la caché de disco por defecto de Coil, así los GIF ya
 * vistos se siguen mostrando sin internet.
 */
fun crearCargadorImagenes(contexto: PlatformContext): ImageLoader =
    ImageLoader.Builder(contexto)
        .components {
            add(KtorNetworkFetcherFactory())
            agregarDecodificadorGif()
        }
        .build()
