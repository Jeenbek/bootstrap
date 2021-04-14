package com.bootstrap.navigation

import com.bootstrap.extensions.screenFlow
import com.bootstrap.auth.intro.IntroFragment
import com.bootstrap.main.MainFragment

class Screens {
    fun authFlow() = screenFlow<IntroFragment>()
    fun mainFlow() = screenFlow<MainFragment>()
}