package com.project.lolstudio.core.di

import com.project.lolstudio.game.data.liveclient.LiveClientGameRepository
import com.project.lolstudio.game.data.liveclient.createLiveClientHttpClient
import com.project.lolstudio.game.domain.repository.LiveGameRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<LiveGameRepository> { LiveClientGameRepository(createLiveClientHttpClient()) }
}
