package com.bootstrap.extensions

import android.app.ProgressDialog
import com.bootstrap.AppActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.KoinContextHandler
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.load(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    withDelay: Boolean = false,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = launch(context, start, block)
    launch(Dispatchers.Main.immediate) {
        val activityContext = KoinContextHandler.getOrNull()?.get<AppActivity>()
        if (withDelay) {
            activityContext?.showLoadingView()
            job.invokeOnCompletion { activityContext?.hideLoadingView() }
        } else {
            val dialog = ProgressDialog(activityContext).apply { show() }
            job.invokeOnCompletion { dialog.dismiss() }
        }
    }
}