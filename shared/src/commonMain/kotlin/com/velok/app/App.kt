package com.velok.app

import androidx.compose.runtime.Composable
import com.velok.app.presentation.navigation.NavegacionRaiz
import com.velok.app.presentation.theme.VelokTheme

@Composable
fun App() {
    VelokTheme {
        NavegacionRaiz()
    }
}
