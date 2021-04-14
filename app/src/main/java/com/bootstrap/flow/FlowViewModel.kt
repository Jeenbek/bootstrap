package com.bootstrap.flow

import com.bootstrap.base.BaseViewModel
import org.koin.core.inject
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

open class FlowViewModel(private vararg val chain: Screen) : BaseViewModel() {
    private val cicerone by inject<Cicerone<Router>>()
    open val navigatorHolder by lazy { cicerone.navigatorHolder }
    open val childRouter by lazy { cicerone.router }
    open fun onStart() = childRouter.newRootChain(*chain)
}