package com.cuervolu.potato

import android.app.Application
import com.cuervolu.potato.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PotatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PotatoApplication)
            modules(appModule)
        }
    }
}