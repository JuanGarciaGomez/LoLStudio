package com.project.lolstudio.recommendations.domain.model

data class EnemyDamage(
    val type: DamageType,
    val magicRatio: Double,
    val champions: List<ChampionDamage>,
)

data class ChampionDamage(
    val championName: String,
    val type: DamageType,
)
