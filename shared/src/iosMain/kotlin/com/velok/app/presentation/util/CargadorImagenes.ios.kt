package com.velok.app.presentation.util

import coil3.ComponentRegistry

// Implementación iOS de expect/actual: no hay decodificador de GIF animado para iOS en Coil
// (coil-gif 3.6.3 solo publica el artefacto de Android). Coil muestra el primer cuadro del GIF
// como imagen fija.
actual fun ComponentRegistry.Builder.agregarDecodificadorGif() = Unit
