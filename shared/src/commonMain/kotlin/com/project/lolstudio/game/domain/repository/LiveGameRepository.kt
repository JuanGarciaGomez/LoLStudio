package com.project.lolstudio.game.domain.repository

import com.project.lolstudio.game.domain.model.LiveGame

interface LiveGameRepository {
    suspend fun fetchCurrentGame(): LiveGame?
}
