package com.velok.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

/**
 * Inicia Koin una sola vez. Cada plataforma puede agregar su propia configuración
 * (por ejemplo `androidContext` en Android).
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        config?.invoke(this)
        modules(velokModules)
    }
}
