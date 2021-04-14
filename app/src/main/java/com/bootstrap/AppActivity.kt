package com.bootstrap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bootstrap.databinding.ActivityBinding
import com.bootstrap.extensions.hide
import com.bootstrap.extensions.show
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class AppActivity : AppCompatActivity() {

    private val navigator by lazy { SupportAppNavigator(this, R.id.container) }
    private val viewModel: AppViewModel by viewModel()
    private lateinit var binding: ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        viewModel.navigatorHolder.setNavigator(navigator)
    }

    fun showLoadingView() = binding.loadingView.show()
    fun hideLoadingView() = binding.loadingView.hide()

    override fun onPause() {
        super.onPause()
        viewModel.navigatorHolder.removeNavigator()
    }
}