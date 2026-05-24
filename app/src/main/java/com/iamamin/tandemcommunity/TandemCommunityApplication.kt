package com.iamamin.tandemcommunity

import android.app.Application
import com.iamamin.tandemcommunity.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TandemCommunityApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TandemCommunityApplication)
            modules(appModule)
        }
    }
}
