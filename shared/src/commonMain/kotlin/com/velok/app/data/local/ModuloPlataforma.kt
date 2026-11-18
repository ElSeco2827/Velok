package com.velok.app.data.local

import org.koin.core.module.Module

/**
 * Código específico de plataforma (expect/actual): cada plataforma decide DÓNDE vive el archivo
 * de la base de datos y cómo se crea su `RoomDatabase.Builder`.
 *
 * - Android (androidMain): necesita el `Context` de la app y usa `context.getDatabasePath`.
 * - iOS (iosMain): no hay Context; usa la carpeta Documents de la app (`NSDocumentDirectory`).
 *
 * El resultado es un módulo de Koin que registra un `RoomDatabase.Builder<BaseDatosVelok>`; el
 * resto de la configuración es común (ver [crearBaseDatos]).
 */
expect fun moduloPlataforma(): Module
