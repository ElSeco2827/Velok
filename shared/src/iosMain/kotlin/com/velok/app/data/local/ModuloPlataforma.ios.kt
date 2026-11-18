package com.velok.app.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// Implementación iOS de expect/actual: la base de datos vive en la carpeta Documents de la app
// (dentro de su sandbox; iOS la conserva entre aperturas y la respalda en iCloud).

actual fun moduloPlataforma(): Module = module {
    single<RoomDatabase.Builder<BaseDatosVelok>> { constructorBaseDatos() }
}

fun constructorBaseDatos(): RoomDatabase.Builder<BaseDatosVelok> =
    Room.databaseBuilder<BaseDatosVelok>(name = "${carpetaDocumentos()}/$NOMBRE_BASE_DATOS")

@OptIn(ExperimentalForeignApi::class)
private fun carpetaDocumentos(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "No se encontró la carpeta Documents" }
}
