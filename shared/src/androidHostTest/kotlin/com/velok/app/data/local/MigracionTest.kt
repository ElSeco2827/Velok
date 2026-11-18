package com.velok.app.data.local

import android.database.sqlite.SQLiteDatabase
import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.OrigenEjercicio
import com.velok.app.domain.model.ResultadoAuth
import com.velok.app.domain.model.TipoCategoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Migración de la base de datos de la versión 1 a la 2. Se crea una base de datos v1 real a partir
 * del esquema exportado (shared/schemas/.../1.json) con un plan, una sesión y una cuenta, y se abre
 * con el código actual (v2): todo debe seguir ahí.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MigracionTest {

    private val nombre = "migracion.db"

    /** Crea las tablas exactamente como las dejaba la versión 1 de la app. */
    private fun crearBaseDatosV1(): SQLiteDatabase {
        val esquema = Json.parseToJsonElement(File(ESQUEMA_V1).readText()).jsonObject.getValue("database").jsonObject
        val archivo = contexto.getDatabasePath(nombre).also { it.parentFile?.mkdirs() }
        val bd = SQLiteDatabase.openOrCreateDatabase(archivo, null)
        for (entidad in esquema.getValue("entities").jsonArray.map { it.jsonObject }) {
            val tabla = entidad.getValue("tableName").jsonPrimitive.content
            bd.execSQL(entidad.getValue("createSql").jsonPrimitive.content.replace("\${TABLE_NAME}", tabla))
            entidad["indices"]?.jsonArray?.forEach { indice ->
                bd.execSQL(indice.jsonObject.getValue("createSql").jsonPrimitive.content.replace("\${TABLE_NAME}", tabla))
            }
        }
        esquema.getValue("setupQueries").jsonArray.forEach { bd.execSQL(it.jsonPrimitive.content) }
        bd.version = 1
        return bd
    }

    @Test
    fun conservaPlanesSesionesYCuentas() = runTest {
        crearBaseDatosV1().use { v1 ->
            v1.execSQL("INSERT INTO ejercicio VALUES ('sentadilla-barra', 0, 'Sentadilla (Barra)', 'PIERNA', 'BARRA', 'CUADRICEPS', 'ISQUIOTIBIALES,GLUTEOS', 4, 12)")
            v1.execSQL("INSERT INTO plan VALUES ('plan-v1', 0, 'Plan de la v1', 'PIERNA', '2026-10-12')")
            v1.execSQL("INSERT INTO plan_ejercicio VALUES ('plan-v1:0', 'plan-v1', 0, 'sentadilla-barra', 90, 'Bajar lento')")
            v1.execSQL("INSERT INTO serie_plan VALUES ('plan-v1:0', 1, 0, 'NORMAL', 80.0, 10)")
            v1.execSQL("INSERT INTO sesion VALUES ('sesion-v1', 'plan-v1', 'Plan de la v1', 1760000000000, 1760002700000)")
            v1.execSQL("INSERT INTO sesion_ejercicio VALUES ('sesion-v1:0', 'sesion-v1', 0, 'sentadilla-barra', 90, '')")
            v1.execSQL("INSERT INTO serie_sesion VALUES ('sesion-v1:0', 1, 0, 'NORMAL', 82.5, 10, 1, 1, 'NORMAL', 80.0, 10)")
            v1.execSQL(
                "INSERT INTO cuenta VALUES ('usuario-v1', 'Ana', 'ana@velok.app', ?, NULL, '2000-01-15', 'KG', 160, 60.5, 'MANTENER')",
                arrayOf(hashRapido.generar("secreta123")),
            )
            v1.execSQL("INSERT INTO usuario_actual VALUES (0, 'usuario-v1')")
        }

        // Abrir con la versión actual aplica la migración automática 1 -> 2.
        val repos = Repositorios(baseDatosEnArchivo(nombre))
        try {
            val ejercicio = requireNotNull(repos.ejercicios.obtenerEjercicio("sentadilla-barra"))
            assertEquals(OrigenEjercicio.EJEMPLO, ejercicio.origen)
            assertEquals(DatosMock.imagen(TipoCategoria.PIERNA), ejercicio.imagenUrl)

            val plan = requireNotNull(repos.planes.observarPlan("plan-v1").first())
            assertEquals("Plan de la v1", plan.nombre)
            assertEquals("sentadilla-barra", plan.ejercicios.single().ejercicio.id)
            assertEquals("Bajar lento", plan.ejercicios.single().nota)
            assertEquals(80.0, plan.ejercicios.single().series.single().pesoKg)
            // No se volvieron a sembrar los datos de ejemplo: la base ya tenía datos.
            assertEquals(listOf("plan-v1"), repos.planes.observarPlanes().first().map { it.id })

            val sesion = repos.sesiones.observarHistorial().first().single()
            assertEquals(82.5, sesion.ejercicios.single().series.single().pesoKg)
            assertEquals(80.0, sesion.ejercicios.single().series.single().anterior?.pesoKg)

            val usuario = requireNotNull(repos.cuentas.observarUsuarioActual().first())
            assertEquals("Ana", usuario.nombre)
            assertEquals(160, usuario.perfil?.estaturaCm)
            repos.cuentas.cerrarSesion()
            assertIs<ResultadoAuth.Exito>(repos.cuentas.ingresar("ana@velok.app", "secreta123"))

            assertNull(repos.baseDatos.sincronizacionDao().obtener())
        } finally {
            repos.cerrar()
        }

        SQLiteDatabase.openDatabase(contexto.getDatabasePath(nombre).path, null, SQLiteDatabase.OPEN_READONLY).use {
            assertEquals(2, it.version)
        }
    }

    private companion object {
        /** Ruta relativa al módulo shared (directorio de trabajo de las pruebas). */
        const val ESQUEMA_V1 = "schemas/com.velok.app.data.local.BaseDatosVelok/1.json"
    }
}
