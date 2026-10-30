package com.project.lolstudio.staticdata.domain.model

data class ShopItem(
    val id: Int,
    val name: String,
    val tags: Set<ItemTag>,
    val totalGold: Int,
    val imageUrl: String,
) {
    val isBoots: Boolean get() = ItemTag.BOOTS in tags
}

enum class ItemTag {
    ABILITY_HASTE,
    ABILITY_POWER,
    ARMOR,
    ARMOR_PENETRATION,
    ATTACK_SPEED,
    AURA,
    BOOTS,
    CRITICAL_STRIKE,
    DAMAGE,
    HEALTH,
    LIFE_STEAL,
    MAGIC_PENETRATION,
    MAGIC_RESIST,
    MANA,
    MANA_REGEN,
    ON_HIT,
    TENACITY,
}
