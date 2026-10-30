package com.project.lolstudio.recommendations.domain.model

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ChampionRole
import com.project.lolstudio.staticdata.domain.model.ItemTag
import com.project.lolstudio.staticdata.domain.model.ItemTag.ABILITY_HASTE
import com.project.lolstudio.staticdata.domain.model.ItemTag.ABILITY_POWER
import com.project.lolstudio.staticdata.domain.model.ItemTag.ARMOR
import com.project.lolstudio.staticdata.domain.model.ItemTag.ARMOR_PENETRATION
import com.project.lolstudio.staticdata.domain.model.ItemTag.ATTACK_SPEED
import com.project.lolstudio.staticdata.domain.model.ItemTag.AURA
import com.project.lolstudio.staticdata.domain.model.ItemTag.CRITICAL_STRIKE
import com.project.lolstudio.staticdata.domain.model.ItemTag.DAMAGE
import com.project.lolstudio.staticdata.domain.model.ItemTag.HEALTH
import com.project.lolstudio.staticdata.domain.model.ItemTag.MAGIC_PENETRATION
import com.project.lolstudio.staticdata.domain.model.ItemTag.MAGIC_RESIST
import com.project.lolstudio.staticdata.domain.model.ItemTag.MANA
import com.project.lolstudio.staticdata.domain.model.ItemTag.MANA_REGEN
import com.project.lolstudio.staticdata.domain.model.ItemTag.ON_HIT
import com.project.lolstudio.staticdata.domain.model.ShopItem

enum class BuildArchetype(
    val bootsTag: ItemTag,
    private val tagWeights: Map<ItemTag, Int>,
) {
    MAGE(
        bootsTag = MAGIC_PENETRATION,
        tagWeights = magicPenalties + mapOf(ABILITY_POWER to 3, MAGIC_PENETRATION to 2, ABILITY_HASTE to 1, HEALTH to -1),
    ),
    MARKSMAN(
        bootsTag = ATTACK_SPEED,
        tagWeights = physicalPenalties + mapOf(CRITICAL_STRIKE to 3, ATTACK_SPEED to 2, DAMAGE to 1, HEALTH to -2),
    ),
    ASSASSIN(
        bootsTag = ABILITY_HASTE,
        tagWeights = physicalPenalties + mapOf(DAMAGE to 3, ARMOR_PENETRATION to 2, ABILITY_HASTE to 1),
    ),
    FIGHTER(
        bootsTag = ABILITY_HASTE,
        tagWeights = physicalPenalties + mapOf(DAMAGE to 2, HEALTH to 2, ABILITY_HASTE to 1),
    ),
    TANK(
        bootsTag = ABILITY_HASTE,
        tagWeights = physicalPenalties + magicPenalties +
            mapOf(HEALTH to 3, ARMOR to 1, MAGIC_RESIST to 1, ABILITY_HASTE to 1, ATTACK_SPEED to -2),
    ),
    SUPPORT(
        bootsTag = ABILITY_HASTE,
        tagWeights = mapOf(MANA_REGEN to 2, ABILITY_HASTE to 2, HEALTH to 1, ABILITY_POWER to 1, DAMAGE to -4, CRITICAL_STRIKE to -4),
    );

    fun score(item: ShopItem): Int = item.tags.sumOf { tagWeights[it] ?: 0 }
}

private val physicalPenalties: Map<ItemTag, Int>
    get() = mapOf(ABILITY_POWER to -4, MANA to -1, MANA_REGEN to -2, AURA to -2)

private val magicPenalties: Map<ItemTag, Int>
    get() = mapOf(DAMAGE to -4, CRITICAL_STRIKE to -4, AURA to -2, ON_HIT to -1)

fun Champion.archetype(): BuildArchetype = when {
    primaryRole == ChampionRole.TANK -> BuildArchetype.TANK
    primaryRole == ChampionRole.SUPPORT -> BuildArchetype.SUPPORT
    magicRatio() > 0.5 -> BuildArchetype.MAGE
    primaryRole == ChampionRole.MARKSMAN -> BuildArchetype.MARKSMAN
    primaryRole == ChampionRole.ASSASSIN -> BuildArchetype.ASSASSIN
    else -> BuildArchetype.FIGHTER
}
