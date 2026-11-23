package com.velok.app.data.salud

import com.velok.app.domain.repository.ActividadRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// Implementación Android de expect/actual: datos de actividad desde Health Connect.
actual fun moduloSalud(): Module = module {
    single<ActividadRepository> { HealthConnectActividadRepository(androidContext()) }
}
