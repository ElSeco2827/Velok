package com.velok.app.di

import com.velok.app.data.repository.MockActividadRepository
import com.velok.app.data.repository.MockEjercicioRepository
import com.velok.app.data.repository.MockPlanRepository
import com.velok.app.data.repository.MockSesionRepository
import com.velok.app.data.repository.MockUsuarioRepository
import com.velok.app.domain.repository.ActividadRepository
import com.velok.app.domain.repository.EjercicioRepository
import com.velok.app.domain.repository.PlanRepository
import com.velok.app.domain.repository.SesionRepository
import com.velok.app.domain.repository.UsuarioRepository
import com.velok.app.domain.usecase.CrearPlanUseCase
import com.velok.app.domain.usecase.EliminarPlanUseCase
import com.velok.app.domain.usecase.GuardarPlanUseCase
import com.velok.app.domain.usecase.IniciarSesionUseCase
import com.velok.app.domain.usecase.ObtenerActividadDiariaUseCase
import com.velok.app.domain.usecase.ObtenerCategoriasUseCase
import com.velok.app.domain.usecase.ObtenerComparacionMuscularUseCase
import com.velok.app.domain.usecase.ObtenerEjercicioUseCase
import com.velok.app.domain.usecase.ObtenerEjerciciosUseCase
import com.velok.app.domain.usecase.ObtenerMetricasUseCase
import com.velok.app.domain.usecase.ObtenerPlanUseCase
import com.velok.app.domain.usecase.ObtenerPlanesUseCase
import com.velok.app.domain.usecase.ObtenerProgresoUseCase
import com.velok.app.domain.usecase.ObtenerResumenHorasUseCase
import com.velok.app.domain.usecase.ObtenerUsuarioUseCase
import com.velok.app.domain.usecase.TerminarSesionUseCase
import com.velok.app.presentation.categoria.CategoriaViewModel
import com.velok.app.presentation.dashboard.DashboardViewModel
import com.velok.app.presentation.detalleejercicio.DetalleEjercicioViewModel
import com.velok.app.presentation.detalleplan.DetallePlanViewModel
import com.velok.app.presentation.editarplan.EditarPlanViewModel
import com.velok.app.presentation.ejercicio.EjercicioViewModel
import com.velok.app.presentation.perfil.PerfilViewModel
import com.velok.app.presentation.planes.PlanesViewModel
import com.velok.app.presentation.progreso.ProgresoViewModel
import com.velok.app.presentation.sesion.SesionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.time.Clock

val dataModule = module {
    single<ActividadRepository> { MockActividadRepository() }
    single<EjercicioRepository> { MockEjercicioRepository() }
    single<PlanRepository> { MockPlanRepository() }
    single<SesionRepository> { MockSesionRepository() }
    single<UsuarioRepository> { MockUsuarioRepository() }
}

val domainModule = module {
    single<Clock> { Clock.System }
    factory { ObtenerActividadDiariaUseCase(get(), get()) }
    factory { ObtenerMetricasUseCase(get(), get()) }
    factory { ObtenerCategoriasUseCase(get()) }
    factory { ObtenerEjerciciosUseCase(get()) }
    factory { ObtenerEjercicioUseCase(get()) }
    factory { ObtenerPlanesUseCase(get()) }
    factory { ObtenerPlanUseCase(get()) }
    factory { CrearPlanUseCase(get(), get()) }
    factory { GuardarPlanUseCase(get()) }
    factory { EliminarPlanUseCase(get()) }
    factory { IniciarSesionUseCase(get(), get()) }
    factory { TerminarSesionUseCase(get(), get()) }
    factory { ObtenerProgresoUseCase(get(), get()) }
    factory { ObtenerResumenHorasUseCase(get(), get()) }
    factory { ObtenerComparacionMuscularUseCase(get(), get()) }
    factory { ObtenerUsuarioUseCase(get()) }
}

val presentationModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::EjercicioViewModel)
    viewModelOf(::PlanesViewModel)
    viewModelOf(::ProgresoViewModel)
    viewModelOf(::PerfilViewModel)
    // Pantallas de detalle: reciben el id de la ruta como parámetro (parametersOf en la pantalla).
    viewModel { params -> CategoriaViewModel(params.get(), get()) }
    viewModel { params -> DetallePlanViewModel(params.get(), get()) }
    viewModel { params -> EditarPlanViewModel(params.get(), get(), get(), get(), get()) }
    viewModel { params -> SesionViewModel(params.get(), get(), get(), get()) }
    viewModel { params -> DetalleEjercicioViewModel(params.get(), get(), get()) }
}

val velokModules = listOf(dataModule, domainModule, presentationModule)
