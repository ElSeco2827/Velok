package com.velok.app.data.repository

import com.velok.app.data.local.BaseDatosVelok
import com.velok.app.data.local.SembradorDatos
import com.velok.app.data.local.aDominio
import com.velok.app.data.local.aFilas
import com.velok.app.domain.model.Plan
import com.velok.app.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/** Planes guardados en la base de datos local: sobreviven al cerrar la app. */
class LocalPlanRepository(
    baseDatos: BaseDatosVelok,
    private val sembrador: SembradorDatos,
) : PlanRepository {

    private val dao = baseDatos.planDao()

    // Room vuelve a emitir cuando cambia cualquiera de las tablas del plan.
    override fun observarPlanes(): Flow<List<Plan>> = flow {
        sembrador.asegurarDatos()
        emitAll(dao.observarTodos().map { planes -> planes.map { it.aDominio() } })
    }

    override fun observarPlan(id: String): Flow<Plan?> = flow {
        sembrador.asegurarDatos()
        emitAll(dao.observar(id).map { it?.aDominio() })
    }

    /** Crea o actualiza. Un plan existente conserva su lugar en la lista; uno nuevo va al final. */
    override suspend fun guardarPlan(plan: Plan) {
        sembrador.asegurarDatos()
        val orden = dao.ordenDe(plan.id) ?: (dao.ordenMaximo() + 1)
        val filas = plan.aFilas(orden)
        dao.reemplazar(filas.plan, filas.ejercicios, filas.series)
    }

    /** Borra el plan con sus ejercicios y series (cascada). El historial de sesiones se conserva. */
    override suspend fun eliminarPlan(id: String) {
        sembrador.asegurarDatos()
        dao.borrar(id)
    }
}
