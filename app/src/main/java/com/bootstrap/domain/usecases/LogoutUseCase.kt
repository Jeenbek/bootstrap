package com.cardbazaar.domain.usecases

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.bootstrap.extensions.load
import com.bootstrap.manager.SharedPreferencesManager
import com.bootstrap.network.Api
import com.bootstrap.network.data
import kotlinx.coroutines.GlobalScope

class LogoutUseCase(
    private val api: Api,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val context: Context
) {
    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    operator fun invoke() = GlobalScope.load {
        api.logout().data ?: return@load
        sharedPreferencesManager.clear()
        notificationManager?.cancelAll()
        restartActivity()
    }

    private fun restartActivity() {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
    }
}