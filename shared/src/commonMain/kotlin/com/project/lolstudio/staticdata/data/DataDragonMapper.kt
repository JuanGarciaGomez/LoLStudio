package com.project.lolstudio.staticdata.data

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ChampionRole
import com.project.lolstudio.staticdata.domain.model.ItemTag
import com.project.lolstudio.staticdata.domain.model.ShopItem

private const val SUMMONERS_RIFT_MAP_ID = "11"
private const val MIN_FINISHED_ITEM_GOLD = 2200
private const val TIER_TWO_BOOTS_DEPTH = 2

// Ids from 10000 up are copies of regular items for Arena and other modes.
private const val MODE_VARIANT_MIN_ID = 10_000

fun ChampionListDto.toDomain(): List<Champion> = data.values.map { dto ->
    Champion(
        id = dto.id,
        name = dto.name,
        roles = dto.tags.mapNotNull(::championRoleOf),
        attack = dto.info.attack,
        magic = dto.info.magic,
    )
}

fun ShopItemListDto.toRecommendableItems(imageUrlOf: (fileName: String) -> String): List<ShopItem> =
    data.mapNotNull { (id, dto) ->
        val itemId = id.toIntOrNull() ?: return@mapNotNull null
        if (!dto.isRecommendable(itemId)) return@mapNotNull null
        ShopItem(
            id = itemId,
            name = dto.name,
            tags = dto.tags.mapNotNull(::itemTagOf).toSet(),
            totalGold = dto.gold.total,
            imageUrl = imageUrlOf(dto.image.full),
        )
    }

private fun ShopItemDto.isRecommendable(id: Int): Boolean =
    id < MODE_VARIANT_MIN_ID &&
        maps[SUMMONERS_RIFT_MAP_ID] == true &&
        gold.purchasable &&
        inStore &&
        requiredChampion == null &&
        requiredAlly == null &&
        (isTierTwoBoots() || isFinishedItem())

private fun ShopItemDto.isTierTwoBoots() = "Boots" in tags && depth == TIER_TWO_BOOTS_DEPTH

private fun ShopItemDto.isFinishedItem() = "Boots" !in tags && into.isEmpty() && gold.total >= MIN_FINISHED_ITEM_GOLD

private fun championRoleOf(tag: String): ChampionRole? = when (tag) {
    "Assassin" -> ChampionRole.ASSASSIN
    "Fighter" -> ChampionRole.FIGHTER
    "Mage" -> ChampionRole.MAGE
    "Marksman" -> ChampionRole.MARKSMAN
    "Support" -> ChampionRole.SUPPORT
    "Tank" -> ChampionRole.TANK
    else -> null
}

private fun itemTagOf(tag: String): ItemTag? = when (tag) {
    "AbilityHaste", "CooldownReduction" -> ItemTag.ABILITY_HASTE
    "SpellDamage" -> ItemTag.ABILITY_POWER
    "Armor" -> ItemTag.ARMOR
    "ArmorPenetration" -> ItemTag.ARMOR_PENETRATION
    "AttackSpeed" -> ItemTag.ATTACK_SPEED
    "Aura" -> ItemTag.AURA
    "Boots" -> ItemTag.BOOTS
    "CriticalStrike" -> ItemTag.CRITICAL_STRIKE
    "Damage" -> ItemTag.DAMAGE
    "Health" -> ItemTag.HEALTH
    "LifeSteal" -> ItemTag.LIFE_STEAL
    "MagicPenetration" -> ItemTag.MAGIC_PENETRATION
    "SpellBlock", "MagicResist" -> ItemTag.MAGIC_RESIST
    "Mana" -> ItemTag.MANA
    "ManaRegen" -> ItemTag.MANA_REGEN
    "OnHit" -> ItemTag.ON_HIT
    "Tenacity" -> ItemTag.TENACITY
    else -> null
}
