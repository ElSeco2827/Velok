package com.velok.app.data.local

import android.database.sqlite.SQLiteDatabase
import com.velok.app.data.repository.DatosMock
import com.velok.app.domain.model.FiltroCatalogo
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
 * Migraciones de la base de datos. Cada prueba crea una base de datos real de una versión anterior
 * a partir de su esquema exportado (shared/schemas/.../N.json), le pone datos y la abre con el
 * código actual, que aplica todas las migraciones pendientes (1 -> 2 automática, 2 -> 3 manual).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MigracionTest {

    /** Crea las tablas exactamente como las dejaba la [version] indicada de la app. */
    private fun crearBaseDatos(nombre: String, version: Int): SQLiteDatabase {
        val esquema = Json.parseToJsonElement(File("$CARPETA_ESQUEMAS/$version.json").readText())
            .jsonObject.getValue("database").jsonObject
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
        bd.version = version
        return bd
    }

    private fun versionFinal(nombre: String): Int =
        SQLiteDatabase.openDatabase(contexto.getDatabasePath(nombre).path, null, SQLiteDatabase.OPEN_READONLY).use { it.version }

    @Test
    fun deLaV1ConservaPlanesSesionesYCuentas() = runTest {
        val nombre = "migracion-v1.db"
        crearBaseDatos(nombre, version = 1).use { v1 ->
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
            // También pasó por la v3: la columna de búsqueda quedó llena.
            assertEquals(listOf("sentadilla-barra"), repos.ejercicios.observarEjercicios(FiltroCatalogo(texto = "SENTADILLA")).first().map { it.id })
        } finally {
            repos.cerrar()
        }
        assertEquals(VERSION_ACTUAL, versionFinal(nombre))
    }

    @Test
    fun deLaV2LlenaLaColumnaDeBusquedaSinTocarLoDemas() = runTest {
        val nombre = "migracion-v2.db"
        crearBaseDatos(nombre, version = 2).use { v2 ->
            v2.execSQL("INSERT INTO ejercicio VALUES ('curl-biceps', 14, 'Curl de bíceps (Mancuernas)', 'BRAZOS', 'PESA', 'BICEPS', '', 4, 12, NULL, 'EJEMPLO')")
            v2.execSQL("INSERT INTO ejercicio VALUES ('jalon-polea', 11, 'Jalón al pecho (Máquina)', 'ESPALDA', 'MAQUINA', 'DORSALES', '', 4, 12, NULL, 'EJEMPLO')")
            v2.execSQL("INSERT INTO ejercicio VALUES ('0dCyly0', 0, 'Barbell seated bradford rocky press', 'HOMBROS', 'BARRA', 'HOMBROS', '', 4, 12, 'https://static.exercisedb.dev/media/0dCyly0.gif', 'EXERCISEDB')")
            v2.execSQL("INSERT INTO plan VALUES ('plan-v2', 0, 'Brazos', 'BRAZOS', '2026-11-01')")
            v2.execSQL("INSERT INTO plan_ejercicio VALUES ('plan-v2:0', 'plan-v2', 0, 'curl-biceps', 60, '')")
            v2.execSQL("INSERT INTO serie_plan VALUES ('plan-v2:0', 1, 0, 'NORMAL', 12.5, 12)")
            v2.execSQL("INSERT INTO sincronizacion_catalogo VALUES (0, 1794978612050, NULL, 0)")
        }

        val repos = Repositorios(baseDatosEnArchivo(nombre))
        try {
            val dao = repos.baseDatos.ejercicioDao()
            assertEquals("curl de biceps (mancuernas)", dao.porId("curl-biceps")?.nombreBusqueda)
            assertEquals("jalon al pecho (maquina)", dao.porId("jalon-polea")?.nombreBusqueda)
            assertEquals("barbell seated bradford rocky press", dao.porId("0dCyly0")?.nombreBusqueda)

            // El catálogo visible (ExerciseDB) se puede buscar; los de ejemplo siguen para el plan.
            assertEquals(listOf("0dCyly0"), repos.ejercicios.observarEjercicios(FiltroCatalogo(texto = "BRADFORD")).first().map { it.id })
            val plan = requireNotNull(repos.planes.observarPlan("plan-v2").first())
            assertEquals("Curl de bíceps (Mancuernas)", plan.ejercicios.single().ejercicio.nombre)
            assertEquals(12.5, plan.ejercicios.single().series.single().pesoKg)
            assertEquals(1794978612050, repos.baseDatos.sincronizacionDao().obtener()?.ultimaDescargaMs)
        } finally {
            repos.cerrar()
        }
        assertEquals(VERSION_ACTUAL, versionFinal(nombre))
    }

    private companion object {
        /** Ruta relativa al módulo shared (directorio de trabajo de las pruebas). */
        const val CARPETA_ESQUEMAS = "schemas/com.velok.app.data.local.BaseDatosVelok"
        const val VERSION_ACTUAL = 3
    }
}
