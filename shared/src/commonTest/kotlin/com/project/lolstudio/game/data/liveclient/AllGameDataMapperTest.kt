package com.project.lolstudio.game.data.liveclient

import com.project.lolstudio.game.domain.model.Team
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class AllGameDataMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun splitsPlayersIntoMeAlliesAndEnemies() {
        val game = json.decodeFromString<AllGameDataDto>(ALL_GAME_DATA).toDomain()

        assertEquals("Me#LAN", game.me.riotId)
        assertEquals(listOf("Ally#LAN"), game.allies.map { it.riotId })
        assertEquals(listOf("Enemy#LAN"), game.enemies.map { it.riotId })
        assertEquals(Team.BLUE, game.me.team)
    }

    @Test
    fun mapsChampionIdFromRawChampionName() {
        val game = json.decodeFromString<AllGameDataDto>(ALL_GAME_DATA).toDomain()

        assertEquals("MonkeyKing", game.me.championId)
        assertEquals("Wukong", game.me.championName)
    }

    @Test
    fun mapsGameTimeAndGold() {
        val game = json.decodeFromString<AllGameDataDto>(ALL_GAME_DATA).toDomain()

        assertEquals(754.5.seconds, game.elapsed)
        assertEquals(1234, game.myGold)
        assertEquals(listOf(1055), game.me.items?.map { it.id })
    }
}

private val ALL_GAME_DATA = """
{
  "activePlayer": { "riotId": "Me#LAN", "level": 9, "currentGold": 1234.7, "championStats": {} },
  "allPlayers": [
    ${player("Me#LAN", "Wukong", "MonkeyKing", "ORDER", items = """[{ "itemID": 1055, "displayName": "Doran's Blade", "count": 1 }]""")},
    ${player("Ally#LAN", "Lux", "Lux", "ORDER")},
    ${player("Enemy#LAN", "Zed", "Zed", "CHAOS")}
  ],
  "events": { "Events": [] },
  "gameData": { "gameMode": "CLASSIC", "gameTime": 754.5, "mapNumber": 11 }
}
"""

private fun player(riotId: String, name: String, rawName: String, team: String, items: String = "[]") = """
{
  "riotId": "$riotId",
  "championName": "$name",
  "rawChampionName": "game_character_displayname_$rawName",
  "team": "$team",
  "level": 9,
  "items": $items,
  "scores": { "kills": 1, "deaths": 2, "assists": 3, "creepScore": 80, "wardScore": 5.0 }
}
"""
