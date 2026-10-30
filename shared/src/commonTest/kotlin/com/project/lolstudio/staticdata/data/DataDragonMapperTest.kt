package com.project.lolstudio.staticdata.data

import com.project.lolstudio.staticdata.domain.model.ChampionRole
import com.project.lolstudio.staticdata.domain.model.ItemTag
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DataDragonMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun keepsOnlyFinishedItemsAndTierTwoBootsFromSummonersRift() {
        val items = json.decodeFromString<ShopItemListDto>(ITEMS).toRecommendableItems { "img/$it" }

        assertEquals(setOf(3047, 3157), items.map { it.id }.toSet())
    }

    @Test
    fun mapsDataDragonTagsToDomainTags() {
        val zhonya = json.decodeFromString<ShopItemListDto>(ITEMS).toRecommendableItems { "img/$it" }
            .single { it.id == 3157 }

        assertEquals(setOf(ItemTag.ARMOR, ItemTag.ABILITY_POWER), zhonya.tags)
        assertEquals("img/Zhonya.png", zhonya.imageUrl)
    }

    @Test
    fun mapsChampionRolesInOrder() {
        val champions = json.decodeFromString<ChampionListDto>(CHAMPIONS).toDomain()

        assertEquals(listOf(ChampionRole.FIGHTER, ChampionRole.TANK), champions.single().roles)
    }
}

private val CHAMPIONS = """
{ "data": { "MonkeyKing": {
  "id": "MonkeyKing", "name": "Wukong", "tags": ["Fighter", "Tank"],
  "info": { "attack": 8, "defense": 5, "magic": 2, "difficulty": 3 }
} } }
"""

private val ITEMS = """
{ "data": {
  "1001": ${item("Botas", 300, """["Boots"]""", into = """["3047"]""")},
  "3047": ${item("Punteras", 1200, """["Armor", "Boots"]""", into = """["3174"]""", depth = 2)},
  "3174": ${item("Blindadas", 1200, """["Armor", "Boots"]""", depth = 3)},
  "1055": ${item("Espada de Doran", 450, """["Damage", "Lane"]""")},
  "3157": ${item("Zhonya", 3250, """["Armor", "SpellDamage", "Active"]""", depth = 3)},
  "223157": ${item("Zhonya Arena", 3250, """["Armor", "SpellDamage"]""", depth = 3)},
  "3600": ${item("Solo ARAM", 3000, """["Damage"]""", depth = 3, maps = """{ "11": false, "12": true }""")}
} }
"""

private fun item(
    name: String,
    gold: Int,
    tags: String,
    into: String = "[]",
    depth: Int? = null,
    maps: String = """{ "11": true }""",
) = """
{
  "name": "$name", "tags": $tags, "into": $into, ${depth?.let { "\"depth\": $it," } ?: ""}
  "gold": { "total": $gold, "purchasable": true },
  "image": { "full": "$name.png" },
  "maps": $maps
}
"""
