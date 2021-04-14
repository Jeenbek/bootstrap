package com.bootstrap.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bootstrap.extensions.BaseVMFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<out T : BaseViewModel>(@LayoutRes layout: Int) : Fragment(layout) {
    private val vmClass by lazy {
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        parameterizedType.actualTypeArguments.first() as Class<T>
    }
    open val viewModel: T by viewModel(vmClass.kotlin)

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as? BaseVMFragment)?.viewModel?.router = getRouter()
    }

    open fun getRouter() = viewModel.router

    protected inline fun <reified T> param(): Lazy<T?> =
        lazy { arguments?.getSerializable(T::class.qualifiedName) as? T }

    protected inline fun <reified T> paramNotNull(): Lazy<T> =
        lazy { arguments?.getSerializable(T::class.qualifiedName) as T }

    fun <T> LiveData<T>.observe(on: (T) -> Unit) = observe(viewLifecycleOwner, Observer(on))
}