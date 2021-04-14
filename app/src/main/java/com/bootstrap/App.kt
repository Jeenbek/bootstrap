package com.bootstrap

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import coil.Coil
import coil.ImageLoader
import com.bootstrap.auth.authModule
import com.bootstrap.network.networkModule
import org.koin.android.BuildConfig
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        startKoin {
            androidContext(this@App)
            modules(
                mainModule,
                authModule,
                networkModule,
            )
        }
        Coil.setImageLoader(get<ImageLoader>())
    }

    private fun initLogger() {
        val tree = if (BuildConfig.DEBUG) Timber.DebugTree() else return
        Timber.plant(tree)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}