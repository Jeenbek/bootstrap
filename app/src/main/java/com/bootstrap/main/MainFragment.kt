package com.bootstrap.main

import android.os.Bundle
import android.view.View
import com.bootstrap.R
import com.bootstrap.custom.viewBinding
import com.bootstrap.databinding.FragmentMainBinding
import com.bootstrap.flow.FlowFragment
import com.bootstrap.flow.FlowViewModel
import com.bootstrap.navigation.Screens
import org.koin.android.ext.android.get

class MainFragment : FlowFragment<MainViewModel>(R.layout.fragment_main) {

    private val screens by lazy {
        get<Screens>().run {
            mapOf(
                R.id.navigation_wallet to mainFlow(),
            )
        }
    }

    private val screenKeys by lazy { screens.map { it.value.screenKey } }

    override val navigator: MainNavigator by lazy {
        MainNavigator(requireActivity(), childFragmentManager, R.id.container, viewModel::exit)
    }

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            navigation.apply {
                setOnNavigationItemSelectedListener {
                    screens[it.itemId]?.let(viewModel::navigateTo)
                    true
                }
                setOnNavigationItemReselectedListener {
                    (currentFragment as? FlowFragment<FlowViewModel>)?.viewModel?.onStart()
                }
            }

            childFragmentManager.addOnBackStackChangedListener {
                val topScreen = navigator.topScreenName()
                val selectedIndex = screenKeys.indexOf(topScreen)
                val checkedIndex = if (selectedIndex > -1) selectedIndex else 0
                navigation.menu.getItem(checkedIndex).isChecked = true
            }
        }
    }
}