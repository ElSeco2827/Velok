package com.velok.app

import android.app.Application
import com.velok.app.di.initKoin
import org.koin.android.ext.koin.androidContext

class VelokApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@VelokApplication)
        }
    }
}
