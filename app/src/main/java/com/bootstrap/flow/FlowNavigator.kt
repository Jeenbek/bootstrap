package com.bootstrap.flow

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import timber.log.Timber

open class FlowNavigator(
    activity: FragmentActivity,
    fm: FragmentManager,
    @IdRes containerId: Int,
    private val onExit: () -> Unit
) : SupportAppNavigator(activity, fm, containerId) {
    override fun activityBack() = onExit()
    override fun applyCommand(command: Command) {
        super.applyCommand(command)
        val screen = when (command) {
            is Forward -> command.screen.screenKey
            is Replace -> command.screen.screenKey
            is BackTo -> command.screen?.screenKey
            else -> null
        }
        Timber.e("%s %s", command::class.simpleName, screen)
    }
}