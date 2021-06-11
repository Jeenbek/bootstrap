package com.bootstrap.auth

import com.bootstrap.auth.signIn.SignInViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel<SignInViewModel>()
}