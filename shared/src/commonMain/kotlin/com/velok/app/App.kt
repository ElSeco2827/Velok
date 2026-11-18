package com.velok.app

import androidx.compose.runtime.Composable
import coil3.compose.setSingletonImageLoaderFactory
import com.velok.app.presentation.navigation.NavegacionRaiz
import com.velok.app.presentation.theme.VelokTheme
import com.velok.app.presentation.util.crearCargadorImagenes

@Composable
fun App() {
    // Configura Coil una vez para toda la app (descarga con Ktor y GIF animados en Android).
    setSingletonImageLoaderFactory(::crearCargadorImagenes)
    VelokTheme {
        NavegacionRaiz()
    }
}
