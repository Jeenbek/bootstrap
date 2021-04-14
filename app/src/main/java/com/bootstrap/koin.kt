package com.bootstrap

import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import com.bootstrap.base.BaseViewModel
import com.bootstrap.main.MainViewModel
import com.bootstrap.manager.SharedPreferencesManager
import com.bootstrap.navigation.Screens
import org.koin.android.BuildConfig
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.single
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

val mainModule = module {
    factory { Cicerone.create() }
    single(named(CR_APP)) { get<Cicerone<Router>>() }
    single(named(CR_APP_ROUTER)) { get<Cicerone<Router>>(named(CR_APP)).router }
    single(named(CR_APP_HOLDER)) { get<Cicerone<Router>>(named(CR_APP)).navigatorHolder }

    single(named(CR_MAIN)) { get<Cicerone<Router>>() }
    single(named(CR_MAIN_ROUTER)) { get<Cicerone<Router>>(named(CR_MAIN)).router }
    single(named(CR_MAIN_HOLDER)) { get<Cicerone<Router>>(named(CR_MAIN)).navigatorHolder }

    single<SharedPreferencesManager>()
    single<Screens>()

    viewModel<AppViewModel>()
    viewModel<MainViewModel>()
    viewModel<BaseViewModel>()

    single { get<Context>().getSharedPreferences("shared_preferences", Context.MODE_PRIVATE) }
    single {
        ImageLoader.Builder(get())
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
            .build()
    }
}