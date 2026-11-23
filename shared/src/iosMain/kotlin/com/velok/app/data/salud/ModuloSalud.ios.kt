package com.velok.app.data.salud

import com.velok.app.domain.repository.ActividadRepository
import org.koin.core.module.Module
import org.koin.dsl.module

// Implementación iOS de expect/actual: datos de actividad desde HealthKit.
actual fun moduloSalud(): Module = module {
    single<ActividadRepository> { HealthKitActividadRepository() }
}
