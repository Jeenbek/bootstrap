package com.bootstrap

import com.bootstrap.flow.FlowViewModel
import com.bootstrap.manager.SharedPreferencesManager
import com.bootstrap.navigation.Screens
import org.koin.core.inject
import org.koin.core.qualifier.named
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class AppViewModel(
    screens: Screens,
    private val sharedPreferencesManager: SharedPreferencesManager
) : FlowViewModel(screens.mainFlow()) {
    override val childRouter: Router by inject(named(CR_APP_ROUTER))
    override val navigatorHolder: NavigatorHolder by inject(named(CR_APP_HOLDER))

    override fun onStart() {
        childRouter.newRootScreen(screens.run {
            if (sharedPreferencesManager.token.isNullOrEmpty()) mainFlow() else mainFlow()
        })
    }
}