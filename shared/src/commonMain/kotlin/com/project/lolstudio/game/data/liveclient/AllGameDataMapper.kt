package com.project.lolstudio.game.data.liveclient

import com.project.lolstudio.game.domain.model.Item
import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.game.domain.model.Player
import com.project.lolstudio.game.domain.model.Score
import com.project.lolstudio.game.domain.model.Team
import kotlin.time.Duration.Companion.seconds

private const val RAW_CHAMPION_NAME_PREFIX = "game_character_displayname_"

fun AllGameDataDto.toDomain(): LiveGame {
    val players = allPlayers.map { it.toDomain() }
    val myRiotId = activePlayer.riotId.ifEmpty { activePlayer.summonerName }
    val me = players.firstOrNull { it.riotId == myRiotId }
        ?: error("Active player $myRiotId not found in player list")

    return LiveGame(
        mode = gameData.gameMode,
        elapsed = gameData.gameTime.seconds,
        me = me,
        allies = players.filter { it.team == me.team && it != me },
        enemies = players.filter { it.team != me.team },
        myGold = activePlayer.currentGold.toInt(),
    )
}

private fun PlayerDto.toDomain() = Player(
    riotId = riotId.ifEmpty { summonerName },
    championId = rawChampionName.removePrefix(RAW_CHAMPION_NAME_PREFIX),
    championName = championName,
    team = team.toTeam(),
    level = level,
    items = items.map { Item(id = it.itemId, name = it.displayName) },
    score = Score(
        kills = scores.kills,
        deaths = scores.deaths,
        assists = scores.assists,
        creepScore = scores.creepScore,
    ),
)

private fun String.toTeam() = when (this) {
    "ORDER" -> Team.BLUE
    "CHAOS" -> Team.RED
    else -> error("Unknown team $this")
}
