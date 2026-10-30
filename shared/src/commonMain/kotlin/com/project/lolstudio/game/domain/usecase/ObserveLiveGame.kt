package com.project.lolstudio.game.domain.usecase

import com.project.lolstudio.game.domain.model.LiveGameStatus
import com.project.lolstudio.game.domain.repository.LiveGameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ObserveLiveGame(
    private val repository: LiveGameRepository,
    private val pollInterval: Duration = 3.seconds,
) {
    operator fun invoke(): Flow<LiveGameStatus> = flow {
        while (true) {
            emit(currentStatus())
            delay(pollInterval)
        }
    }.distinctUntilChanged()

    private suspend fun currentStatus(): LiveGameStatus =
        try {
            repository.fetchCurrentGame()
                ?.let(LiveGameStatus::InGame)
                ?: LiveGameStatus.NoGame
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LiveGameStatus.Unavailable(e.message ?: e::class.simpleName.orEmpty())
        }
}
