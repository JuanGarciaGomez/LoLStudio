package com.project.lolstudio.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.lolstudio.game.domain.model.LiveGameStatus
import com.project.lolstudio.game.domain.usecase.ObserveLiveGame
import com.project.lolstudio.recommendations.domain.usecase.RecommendBuild
import com.project.lolstudio.staticdata.domain.usecase.GetChampionPortraitUrls
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LiveGameViewModel(
    observeLiveGame: ObserveLiveGame,
    private val getChampionPortraitUrls: GetChampionPortraitUrls,
    private val recommendBuild: RecommendBuild,
) : ViewModel() {

    val state: StateFlow<LiveGameState> = observeLiveGame()
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LiveGameState.Loading)

    private suspend fun LiveGameStatus.toState() = when (this) {
        LiveGameStatus.NoGame -> LiveGameState.WaitingForGame
        is LiveGameStatus.InGame -> LiveGameState.InGame(
            game = game,
            championPortraitUrls = getChampionPortraitUrls(game.players.map { it.championId }),
            recommendation = recommendBuild(game),
        )
        is LiveGameStatus.Unavailable -> LiveGameState.Error(reason)
    }
}
