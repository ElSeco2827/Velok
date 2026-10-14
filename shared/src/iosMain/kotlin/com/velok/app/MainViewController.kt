package com.velok.app

import androidx.compose.ui.window.ComposeUIViewController
import com.velok.app.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = { initKoin() }) { App() }
