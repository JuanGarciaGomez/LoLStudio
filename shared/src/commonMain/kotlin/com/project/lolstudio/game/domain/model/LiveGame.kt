package com.project.lolstudio.game.domain.model

import kotlin.time.Duration

data class LiveGame(
    val mode: String,
    val elapsed: Duration,
    val me: Player,
    val allies: List<Player>,
    val enemies: List<Player>,
    val myGold: Int?,
)

data class Player(
    val riotId: String,
    val championId: String,
    val championName: String,
    val team: Team,
    val level: Int?,
    val items: List<Item>?,
    val score: Score?,
)

enum class Team { BLUE, RED }

data class Item(
    val id: Int,
    val name: String,
)

data class Score(
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val creepScore: Int,
)
