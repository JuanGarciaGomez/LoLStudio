package com.project.lolstudio.recommendations.domain.usecase

import com.project.lolstudio.game.domain.model.Player
import com.project.lolstudio.recommendations.domain.model.ChampionDamage
import com.project.lolstudio.recommendations.domain.model.DamageType
import com.project.lolstudio.recommendations.domain.model.EnemyDamage
import com.project.lolstudio.recommendations.domain.model.damageType
import com.project.lolstudio.staticdata.domain.model.Champion

class ClassifyEnemyDamage {

    operator fun invoke(enemies: List<Player>, champions: Map<String, Champion>): EnemyDamage {
        val damages = enemies.mapNotNull { enemy ->
            champions[enemy.championId.lowercase()]?.let { ChampionDamage(it.name, it.damageType()) }
        }
        val magicRatio = damages.map { it.type.magicWeight }.average().takeUnless { it.isNaN() } ?: 0.5
        return EnemyDamage(
            type = DamageType.fromMagicRatio(magicRatio),
            magicRatio = magicRatio,
            champions = damages,
        )
    }
}
