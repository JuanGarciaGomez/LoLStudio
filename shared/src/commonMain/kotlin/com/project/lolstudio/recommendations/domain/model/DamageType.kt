package com.project.lolstudio.recommendations.domain.model

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ChampionRole

private const val MAGIC_THRESHOLD = 0.6
private const val PHYSICAL_THRESHOLD = 0.4

enum class DamageType(val magicWeight: Double) {
    PHYSICAL(0.0),
    MIXED(0.5),
    MAGIC(1.0);

    companion object {
        fun fromMagicRatio(ratio: Double): DamageType = when {
            ratio >= MAGIC_THRESHOLD -> MAGIC
            ratio <= PHYSICAL_THRESHOLD -> PHYSICAL
            else -> MIXED
        }
    }
}

fun Champion.damageType(): DamageType = DamageType.fromMagicRatio(magicRatio())

fun Champion.magicRatio(): Double {
    val total = attack + magic
    if (total == 0) return if (primaryRole in setOf(ChampionRole.MAGE, ChampionRole.SUPPORT)) 1.0 else 0.0
    return magic.toDouble() / total
}
