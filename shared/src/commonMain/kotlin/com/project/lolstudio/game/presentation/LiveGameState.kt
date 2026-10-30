package com.project.lolstudio.game.presentation

import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.recommendations.domain.model.BuildRecommendation

sealed interface LiveGameState {
    data object Loading : LiveGameState
    data object WaitingForGame : LiveGameState
    data class InGame(
        val game: LiveGame,
        val championPortraitUrls: Map<String, String>,
        val recommendation: BuildRecommendation?,
    ) : LiveGameState
    data class Error(val message: String) : LiveGameState
}
