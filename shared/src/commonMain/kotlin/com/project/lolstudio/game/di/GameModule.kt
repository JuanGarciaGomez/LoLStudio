package com.project.lolstudio.game.di

import com.project.lolstudio.game.domain.usecase.ObserveLiveGame
import com.project.lolstudio.game.presentation.LiveGameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gameModule = module {
    factory { ObserveLiveGame(repository = get()) }
    viewModelOf(::LiveGameViewModel)
}
