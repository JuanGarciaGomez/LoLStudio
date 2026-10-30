package com.project.lolstudio.recommendations.domain.model

import com.project.lolstudio.staticdata.domain.model.ShopItem

data class BuildRecommendation(
    val archetype: BuildArchetype,
    val enemyDamage: EnemyDamage,
    val boots: ShopItem?,
    val core: List<ShopItem>,
    val defensive: List<ShopItem>,
)
