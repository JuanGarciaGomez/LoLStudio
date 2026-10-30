package com.project.lolstudio.recommendations.domain.usecase

import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.recommendations.domain.model.BuildArchetype
import com.project.lolstudio.recommendations.domain.model.BuildRecommendation
import com.project.lolstudio.recommendations.domain.model.DamageType
import com.project.lolstudio.recommendations.domain.model.archetype
import com.project.lolstudio.staticdata.domain.model.ItemTag
import com.project.lolstudio.staticdata.domain.model.ShopItem
import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import kotlin.coroutines.cancellation.CancellationException

private const val CORE_ITEM_COUNT = 3
private const val DEFENSIVE_ITEM_COUNT = 2

class RecommendBuild(
    private val repository: StaticDataRepository,
    private val classifyEnemyDamage: ClassifyEnemyDamage,
) {

    suspend operator fun invoke(game: LiveGame): BuildRecommendation? =
        try {
            recommend(game)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

    private suspend fun recommend(game: LiveGame): BuildRecommendation? {
        val champions = repository.champions().associateBy { it.id.lowercase() }
        val myChampion = champions[game.me.championId.lowercase()] ?: return null
        val archetype = myChampion.archetype()
        val enemyDamage = classifyEnemyDamage(game.enemies, champions)

        val ownedItemIds = game.me.items.orEmpty().map { it.id }.toSet()
        val (boots, finishedItems) = repository.recommendableItems()
            .filterNot { it.id in ownedItemIds }
            .partition { it.isBoots }

        val defensive = finishedItems.defensiveFor(archetype, enemyDamage.type)
        val core = finishedItems
            .filter { archetype == BuildArchetype.TANK || it.tags.none(ItemTag::isResistance) }
            .rankedFor(archetype)
            .filterNot { it in defensive }
            .take(CORE_ITEM_COUNT)

        return BuildRecommendation(
            archetype = archetype,
            enemyDamage = enemyDamage,
            boots = boots.bootsFor(archetype, enemyDamage.type),
            core = core,
            defensive = defensive,
        )
    }

    private fun List<ShopItem>.defensiveFor(archetype: BuildArchetype, damageType: DamageType): List<ShopItem> {
        val resistances = damageType.counterResistances()
        val perResistance = DEFENSIVE_ITEM_COUNT / resistances.size
        return resistances.fold(emptyList()) { chosen, resistance ->
            chosen + filter { resistance in it.tags && it !in chosen }
                .rankedFor(archetype)
                .take(perResistance)
        }
    }

    private fun List<ShopItem>.bootsFor(archetype: BuildArchetype, damageType: DamageType): ShopItem? {
        val wantedTag = damageType.counterResistances().singleOrNull() ?: archetype.bootsTag
        return firstOrNull { wantedTag in it.tags }
    }

    private fun List<ShopItem>.rankedFor(archetype: BuildArchetype): List<ShopItem> =
        map { it to archetype.score(it) }
            .filter { (_, score) -> score > 0 }
            .sortedWith(compareByDescending<Pair<ShopItem, Int>> { it.second }.thenByDescending { it.first.totalGold })
            .map { it.first }
}

private fun DamageType.counterResistances(): List<ItemTag> = when (this) {
    DamageType.PHYSICAL -> listOf(ItemTag.ARMOR)
    DamageType.MAGIC -> listOf(ItemTag.MAGIC_RESIST)
    DamageType.MIXED -> listOf(ItemTag.ARMOR, ItemTag.MAGIC_RESIST)
}

private fun ItemTag.isResistance() = this == ItemTag.ARMOR || this == ItemTag.MAGIC_RESIST
