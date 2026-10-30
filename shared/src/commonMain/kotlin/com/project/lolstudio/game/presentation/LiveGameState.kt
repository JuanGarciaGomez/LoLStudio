package com.project.lolstudio.game.presentation

import com.project.lolstudio.game.domain.model.LiveGame

sealed interface LiveGameState {
    data object Loading : LiveGameState
    data object WaitingForGame : LiveGameState
    data class InGame(
        val game: LiveGame,
        val championPortraitUrls: Map<String, String>,
    ) : LiveGameState
    data class Error(val message: String) : LiveGameState
}
