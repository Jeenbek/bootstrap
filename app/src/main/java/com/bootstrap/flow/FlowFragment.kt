package com.bootstrap.flow

import android.content.Context
import androidx.activity.addCallback
import com.bootstrap.R
import com.bootstrap.base.BaseFragment
import ru.terrakok.cicerone.Navigator

abstract class FlowFragment<out T : FlowViewModel>(layoutRes: Int) : BaseFragment<T>(layoutRes) {
    val currentFragment get() = childFragmentManager.findFragmentById(R.id.container)

    protected open val navigator: Navigator by lazy {
        FlowNavigator(requireActivity(), childFragmentManager, R.id.container, viewModel::exit)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.childRouter.exit()
        }
    }

    override fun getRouter() = viewModel.childRouter

    override fun onResume() {
        super.onResume()
        viewModel.navigatorHolder.setNavigator(navigator)
        if (currentFragment == null) viewModel.onStart()
    }

    override fun onPause() {
        super.onPause()
        viewModel.navigatorHolder.removeNavigator()
    }
}