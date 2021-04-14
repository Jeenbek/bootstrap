package com.bootstrap.extensions

import android.os.Bundle
import com.bootstrap.R
import com.bootstrap.flow.FlowFragment
import com.bootstrap.flow.FlowViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.terrakok.cicerone.Screen
import ru.terrakok.cicerone.android.support.SupportAppScreen

inline fun <reified T : BaseVMFragment> screen(args: Bundle? = null) =
    object : SupportAppScreen() {
        private val pScreenKey by lazy { T::class.java.run { canonicalName ?: name } }
        override fun getFragment() = T::class.java.newInstance().apply { arguments = args }
        override fun getScreenKey() = pScreenKey
    }

fun screenFlow(vararg chain: Screen) =
    object : SupportAppScreen() {
        init {
            require(chain.isNotEmpty()) { "chain must not be empty" }
        }
        private val pScreenKey by lazy { chain.first().screenKey + "Flow" }
        override fun getFragment() = ScreenFlowFragment(*chain)
        override fun getScreenKey() = pScreenKey
    }

inline fun <reified T : BaseVMFragment> screenFlow(args: Bundle? = null) =
    screenFlow(screen<T>(args))

class ScreenFlowFragment(vararg chain: Screen) :
    FlowFragment<FlowViewModel>(R.layout.fragment_flow) {
    override val viewModel: FlowViewModel by viewModel { parametersOf(chain) }
}
