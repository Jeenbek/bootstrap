package com.bootstrap.navigation

import com.bootstrap.auth.signIn.SignInFragment
import com.bootstrap.extensions.screenFlow
import com.bootstrap.main.MainFragment

class Screens {
    fun authFlow() = screenFlow<SignInFragment>()
    fun mainFlow() = screenFlow<MainFragment>()
}