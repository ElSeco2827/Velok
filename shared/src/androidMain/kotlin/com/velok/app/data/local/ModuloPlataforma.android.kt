package com.velok.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// Implementación Android de expect/actual: la base de datos vive en la carpeta privada de la app
// (/data/data/com.velok.app/databases/velok.db) y Room necesita el Context para abrirla.

actual fun moduloPlataforma(): Module = module {
    single<RoomDatabase.Builder<BaseDatosVelok>> { constructorBaseDatos(androidContext()) }
}

fun constructorBaseDatos(context: Context): RoomDatabase.Builder<BaseDatosVelok> {
    val app = context.applicationContext
    return Room.databaseBuilder<BaseDatosVelok>(
        context = app,
        name = app.getDatabasePath(NOMBRE_BASE_DATOS).absolutePath,
    )
}
