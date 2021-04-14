package com.bootstrap.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootstrap.CR_APP_ROUTER
import com.bootstrap.custom.PrivateLiveData
import com.bootstrap.navigation.Screens
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import ru.terrakok.cicerone.Router

open class BaseViewModel : ViewModel(), KoinComponent {
    protected val screens by inject<Screens>()
    open lateinit var router: Router
    protected val appRouter: Router by inject(named(CR_APP_ROUTER))
    val vmScope by lazy {
        CoroutineScope(viewModelScope.coroutineContext + get<CoroutineExceptionHandler>())
    }

    open fun exit() = router.exit()

    protected var <T> PrivateLiveData<T>.data
        set(value) = this.set(value)
        get() = this.value

    protected fun <T> PrivateLiveData<T>.update(transform: (T?) -> T?) {
        data = transform(data)
    }
}