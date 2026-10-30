package com.project.lolstudio.game.data.liveclient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllGameDataDto(
    val activePlayer: ActivePlayerDto,
    val allPlayers: List<PlayerDto>,
    val gameData: GameDataDto,
)

@Serializable
data class ActivePlayerDto(
    val riotId: String = "",
    val summonerName: String = "",
    val level: Int,
    val currentGold: Double,
)

@Serializable
data class PlayerDto(
    val riotId: String = "",
    val summonerName: String = "",
    val championName: String,
    val rawChampionName: String,
    val team: String,
    val level: Int,
    val items: List<ItemDto>,
    val scores: ScoresDto,
)

@Serializable
data class ItemDto(
    @SerialName("itemID") val itemId: Int,
    val displayName: String,
)

@Serializable
data class ScoresDto(
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val creepScore: Int,
)

@Serializable
data class GameDataDto(
    val gameMode: String,
    val gameTime: Double,
)
