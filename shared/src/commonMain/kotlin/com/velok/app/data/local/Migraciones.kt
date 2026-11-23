package com.velok.app.data.local

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.velok.app.domain.model.normalizarBusqueda

/**
 * v2 -> v3: columna `nombreBusqueda` para buscar sin acentos. Es manual (no AutoMigration) porque
 * hay que llenarla para los ejercicios que ya están guardados, y quitar acentos solo se puede
 * hacer en Kotlin. Solo toca la tabla ejercicio: planes, sesiones y cuentas no cambian.
 *
 * Se implementa sobre SQLiteConnection (el camino que usa Room con un SQLiteDriver), igual en
 * Android e iOS.
 */
val MIGRACION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `ejercicio` ADD COLUMN `nombreBusqueda` TEXT NOT NULL DEFAULT ''")

        val nombres = mutableListOf<Pair<String, String>>()
        connection.prepare("SELECT `id`, `nombre` FROM `ejercicio`").use { consulta ->
            while (consulta.step()) nombres += consulta.getText(0) to consulta.getText(1)
        }
        connection.prepare("UPDATE `ejercicio` SET `nombreBusqueda` = ? WHERE `id` = ?").use { actualizar ->
            for ((id, nombre) in nombres) {
                actualizar.bindText(1, normalizarBusqueda(nombre))
                actualizar.bindText(2, id)
                actualizar.step()
                actualizar.reset()
            }
        }
    }
}

/** Migraciones manuales; la app y las pruebas las registran igual. */
fun RoomDatabase.Builder<BaseDatosVelok>.conMigraciones(): RoomDatabase.Builder<BaseDatosVelok> =
    addMigrations(MIGRACION_2_3)
