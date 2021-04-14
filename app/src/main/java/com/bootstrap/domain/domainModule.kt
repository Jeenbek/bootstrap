package com.cardbazaar.domain

import com.cardbazaar.domain.usecases.LogoutUseCase
import org.koin.dsl.module
import org.koin.experimental.builder.single

val domainModule = module {
    single<LogoutUseCase>()
}