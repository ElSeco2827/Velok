package com.velok.app.data.repository

import com.velok.app.domain.model.CategoriaEjercicio
import com.velok.app.domain.model.Plan
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.repository.EjercicioRepository
import kotlinx.coroutines.delay
import velok.shared.generated.resources.Res

/**
 * Datos falsos mientras se integra la API de ExerciseDB.
 * Las imágenes son placeholders locales de composeResources expuestos como URI,
 * así la UI las carga con Coil igual que cargará las URLs remotas.
 */
class MockEjercicioRepository : EjercicioRepository {

    override suspend fun obtenerPlanes(): List<Plan> {
        delay(RETRASO_SIMULADO_MS)
        return listOf(
            Plan("plan-pecho", TipoCategoria.PECHO, imagen(TipoCategoria.PECHO), 8),
            Plan("plan-espalda", TipoCategoria.ESPALDA, imagen(TipoCategoria.ESPALDA), 10),
            Plan("plan-pierna", TipoCategoria.PIERNA, imagen(TipoCategoria.PIERNA), 9),
            Plan("plan-brazos", TipoCategoria.BRAZOS, imagen(TipoCategoria.BRAZOS), 7),
        )
    }

    override suspend fun obtenerCategorias(): List<CategoriaEjercicio> {
        delay(RETRASO_SIMULADO_MS)
        return TipoCategoria.entries.map { tipo ->
            CategoriaEjercicio(id = tipo.name.lowercase(), tipo = tipo, imagenUrl = imagen(tipo))
        }
    }

    private fun imagen(tipo: TipoCategoria): String =
        Res.getUri("drawable/categoria_${tipo.name.lowercase()}.jpg")

    private companion object {
        const val RETRASO_SIMULADO_MS = 300L
    }
}
