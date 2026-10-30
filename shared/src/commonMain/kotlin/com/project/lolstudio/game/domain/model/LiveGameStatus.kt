package com.project.lolstudio.game.domain.model

sealed interface LiveGameStatus {
    data object NoGame : LiveGameStatus
    data class InGame(val game: LiveGame) : LiveGameStatus
    data class Unavailable(val reason: String) : LiveGameStatus
}
