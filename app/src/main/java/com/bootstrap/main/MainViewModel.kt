package com.bootstrap.main

import com.bootstrap.CR_MAIN_HOLDER
import com.bootstrap.CR_MAIN_ROUTER
import com.bootstrap.flow.FlowViewModel
import com.bootstrap.navigation.Screens
import org.koin.core.inject
import org.koin.core.qualifier.named
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

class MainViewModel(
    screens: Screens
) : FlowViewModel() {
    override val childRouter: Router by inject(named(CR_MAIN_ROUTER))
    override val navigatorHolder: NavigatorHolder by inject(named(CR_MAIN_HOLDER))
    fun navigateTo(screen: Screen) = childRouter.navigateTo(screen)



}