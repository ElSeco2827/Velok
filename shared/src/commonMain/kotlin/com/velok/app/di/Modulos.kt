package com.velok.app.di

import com.velok.app.data.repository.MockActividadRepository
import com.velok.app.data.repository.MockEjercicioRepository
import com.velok.app.domain.repository.ActividadRepository
import com.velok.app.domain.repository.EjercicioRepository
import com.velok.app.domain.usecase.ObtenerActividadDiariaUseCase
import com.velok.app.domain.usecase.ObtenerCategoriasUseCase
import com.velok.app.domain.usecase.ObtenerMetricasUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import com.velok.app.presentation.dashboard.DashboardViewModel
import com.velok.app.presentation.ejercicio.EjercicioViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<ActividadRepository> { MockActividadRepository() }
    single<EjercicioRepository> { MockEjercicioRepository() }
}

val domainModule = module {
    factory { ObtenerActividadDiariaUseCase(get()) }
    factory { ObtenerMetricasUseCase(get()) }
    factory { ObtenerPlanesUseCase(get()) }
    factory { ObtenerCategoriasUseCase(get()) }
}

val presentationModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::EjercicioViewModel)
}

val velokModules = listOf(dataModule, domainModule, presentationModule)
