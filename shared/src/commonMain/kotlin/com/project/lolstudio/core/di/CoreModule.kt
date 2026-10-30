package com.project.lolstudio.core.di

import com.project.lolstudio.core.network.createHttpClient
import org.koin.dsl.module

val coreModule = module {
    single { createHttpClient() }
}
