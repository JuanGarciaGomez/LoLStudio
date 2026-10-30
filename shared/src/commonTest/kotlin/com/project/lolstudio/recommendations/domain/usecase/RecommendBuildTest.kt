package com.project.lolstudio.recommendations.domain.usecase

import com.project.lolstudio.game.domain.model.Item
import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.game.domain.model.Player
import com.project.lolstudio.game.domain.model.Team
import com.project.lolstudio.recommendations.domain.model.BuildArchetype
import com.project.lolstudio.recommendations.domain.model.DamageType
import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ChampionRole
import com.project.lolstudio.staticdata.domain.model.ItemTag
import com.project.lolstudio.staticdata.domain.model.ItemTag.ABILITY_HASTE
import com.project.lolstudio.staticdata.domain.model.ItemTag.ABILITY_POWER
import com.project.lolstudio.staticdata.domain.model.ItemTag.ARMOR
import com.project.lolstudio.staticdata.domain.model.ItemTag.BOOTS
import com.project.lolstudio.staticdata.domain.model.ItemTag.DAMAGE
import com.project.lolstudio.staticdata.domain.model.ItemTag.MAGIC_PENETRATION
import com.project.lolstudio.staticdata.domain.model.ItemTag.MAGIC_RESIST
import com.project.lolstudio.staticdata.domain.model.ShopItem
import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class RecommendBuildTest {

    private val recommendBuild = RecommendBuild(FakeStaticDataRepository(), ClassifyEnemyDamage())

    @Test
    fun recommendsArmorAgainstPhysicalEnemies() = runTest {
        val recommendation = recommendBuild(gameAs("Ahri", enemies = listOf("Zed", "Darius")))!!

        assertEquals(BuildArchetype.MAGE, recommendation.archetype)
        assertEquals(DamageType.PHYSICAL, recommendation.enemyDamage.type)
        assertEquals("Punteras", recommendation.boots?.name)
        assertEquals(listOf("Zhonya"), recommendation.defensive.map { it.name })
        assertEquals(listOf("Rabadon", "Lumbria"), recommendation.core.map { it.name })
    }

    @Test
    fun recommendsMagicResistAgainstMagicEnemies() = runTest {
        val recommendation = recommendBuild(gameAs("Ahri", enemies = listOf("Lux", "Syndra")))!!

        assertEquals(DamageType.MAGIC, recommendation.enemyDamage.type)
        assertEquals("Mercurio", recommendation.boots?.name)
        assertEquals(listOf("Banshee"), recommendation.defensive.map { it.name })
    }

    @Test
    fun usesArchetypeBootsAgainstMixedDamage() = runTest {
        val recommendation = recommendBuild(gameAs("Ahri", enemies = listOf("Lux", "Zed")))!!

        assertEquals(DamageType.MIXED, recommendation.enemyDamage.type)
        assertEquals("Hechicero", recommendation.boots?.name)
        assertEquals(listOf("Zhonya", "Banshee"), recommendation.defensive.map { it.name })
    }

    @Test
    fun skipsItemsAlreadyOwned() = runTest {
        val game = gameAs("Ahri", enemies = listOf("Zed"), ownedItemIds = listOf(RABADON_ID))

        val recommendation = recommendBuild(game)!!

        assertEquals(listOf("Lumbria"), recommendation.core.map { it.name })
    }

    @Test
    fun returnsNothingForUnknownChampion() = runTest {
        assertNull(recommendBuild(gameAs("NewChampion", enemies = listOf("Zed"))))
    }
}

private const val RABADON_ID = 3089

private class FakeStaticDataRepository : StaticDataRepository {
    override suspend fun championPortraitUrl(championId: String) = ""

    override suspend fun champions() = listOf(
        champion("Ahri", ChampionRole.MAGE, attack = 3, magic = 8),
        champion("Lux", ChampionRole.MAGE, attack = 2, magic = 9),
        champion("Syndra", ChampionRole.MAGE, attack = 2, magic = 9),
        champion("Zed", ChampionRole.ASSASSIN, attack = 9, magic = 1),
        champion("Darius", ChampionRole.FIGHTER, attack = 9, magic = 0),
    )

    override suspend fun recommendableItems() = listOf(
        shopItem(3047, "Punteras", BOOTS, ARMOR),
        shopItem(3111, "Mercurio", BOOTS, MAGIC_RESIST),
        shopItem(3020, "Hechicero", BOOTS, MAGIC_PENETRATION),
        shopItem(RABADON_ID, "Rabadon", ABILITY_POWER, gold = 3500),
        shopItem(4645, "Lumbria", ABILITY_POWER, gold = 3200),
        shopItem(3157, "Zhonya", ARMOR, ABILITY_POWER),
        shopItem(3102, "Banshee", MAGIC_RESIST, ABILITY_POWER),
        shopItem(6333, "Danza", ARMOR, DAMAGE, ABILITY_HASTE),
        shopItem(3031, "Filo", DAMAGE),
    )
}

private fun champion(id: String, role: ChampionRole, attack: Int, magic: Int) =
    Champion(id = id, name = id, roles = listOf(role), attack = attack, magic = magic)

private fun shopItem(id: Int, name: String, vararg tags: ItemTag, gold: Int = 3000) =
    ShopItem(id = id, name = name, tags = tags.toSet(), totalGold = gold, imageUrl = "")

private fun gameAs(championId: String, enemies: List<String>, ownedItemIds: List<Int> = emptyList()) = LiveGame(
    mode = "CLASSIC",
    elapsed = 10.minutes,
    me = player(championId, Team.BLUE, ownedItemIds),
    allies = emptyList(),
    enemies = enemies.map { player(it, Team.RED) },
    myGold = 0,
)

private fun player(championId: String, team: Team, itemIds: List<Int> = emptyList()) = Player(
    riotId = "$championId#TEST",
    championId = championId,
    championName = championId,
    team = team,
    level = 6,
    items = itemIds.map { Item(id = it, name = "") },
    score = null,
)
