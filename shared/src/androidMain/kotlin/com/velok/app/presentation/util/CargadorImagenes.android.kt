package com.velok.app.presentation.util

import android.os.Build
import coil3.ComponentRegistry
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder

// Implementación Android de expect/actual: GIF animados con coil-gif.
actual fun ComponentRegistry.Builder.agregarDecodificadorGif() {
    if (Build.VERSION.SDK_INT >= 28) {
        // Decodificador del sistema (ImageDecoder), más eficiente; existe desde Android 9.
        add(AnimatedImageDecoder.Factory())
    } else {
        // Android 8.x (minSdk 26-27).
        add(GifDecoder.Factory())
    }
}
