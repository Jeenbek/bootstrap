package com.bootstrap.auth

import com.bootstrap.auth.intro.IntroViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel<IntroViewModel>()
}