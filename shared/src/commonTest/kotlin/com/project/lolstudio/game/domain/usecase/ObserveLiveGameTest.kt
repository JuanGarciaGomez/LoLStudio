package com.project.lolstudio.game.domain.usecase

import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.game.domain.model.LiveGameStatus
import com.project.lolstudio.game.domain.model.Player
import com.project.lolstudio.game.domain.model.Team
import com.project.lolstudio.game.domain.repository.LiveGameRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class ObserveLiveGameTest {

    @Test
    fun emitsNoGameWhenRepositoryReturnsNull() = runTest {
        val observeLiveGame = ObserveLiveGame(FakeRepository(listOf { null }))

        assertEquals(LiveGameStatus.NoGame, observeLiveGame().first())
    }

    @Test
    fun emitsUnavailableWhenRepositoryFails() = runTest {
        val observeLiveGame = ObserveLiveGame(FakeRepository(listOf { error("boom") }))

        assertEquals(LiveGameStatus.Unavailable("boom"), observeLiveGame().first())
    }

    @Test
    fun keepsPollingAndSkipsRepeatedStatuses() = runTest {
        val repository = FakeRepository(listOf({ null }, { null }, { aGame }, { null }))
        val observeLiveGame = ObserveLiveGame(repository)

        val statuses = observeLiveGame().take(3).toList()

        assertEquals(
            listOf(LiveGameStatus.NoGame, LiveGameStatus.InGame(aGame), LiveGameStatus.NoGame),
            statuses,
        )
    }
}

private class FakeRepository(private val responses: List<() -> LiveGame?>) : LiveGameRepository {
    private var calls = 0

    override suspend fun fetchCurrentGame(): LiveGame? = responses[calls++ % responses.size]()
}

private val aPlayer = Player(
    riotId = "Me#LAN",
    championId = "Ahri",
    championName = "Ahri",
    team = Team.BLUE,
    level = null,
    items = null,
    score = null,
)

private val aGame = LiveGame(
    mode = "CLASSIC",
    elapsed = 5.minutes,
    me = aPlayer,
    allies = emptyList(),
    enemies = emptyList(),
    myGold = null,
)
