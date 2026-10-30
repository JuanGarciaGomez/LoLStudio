package com.project.lolstudio.recommendations.domain.model

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ChampionRole
import com.project.lolstudio.staticdata.domain.model.ChampionRole.ASSASSIN
import com.project.lolstudio.staticdata.domain.model.ChampionRole.FIGHTER
import com.project.lolstudio.staticdata.domain.model.ChampionRole.MAGE
import com.project.lolstudio.staticdata.domain.model.ChampionRole.MARKSMAN
import com.project.lolstudio.staticdata.domain.model.ChampionRole.SUPPORT
import com.project.lolstudio.staticdata.domain.model.ChampionRole.TANK
import kotlin.test.Test
import kotlin.test.assertEquals

class ChampionRulesTest {

    @Test
    fun classifiesDamageTypeFromAttackAndMagicRatings() {
        assertEquals(DamageType.PHYSICAL, champion(FIGHTER, attack = 8, magic = 2).damageType())
        assertEquals(DamageType.MAGIC, champion(MAGE, attack = 3, magic = 8).damageType())
        assertEquals(DamageType.MIXED, champion(FIGHTER, attack = 7, magic = 7).damageType())
    }

    @Test
    fun fallsBackToRoleWhenRatingsAreMissing() {
        assertEquals(DamageType.MAGIC, champion(SUPPORT, attack = 0, magic = 0).damageType())
        assertEquals(DamageType.PHYSICAL, champion(MARKSMAN, attack = 0, magic = 0).damageType())
    }

    @Test
    fun picksArchetypeFromPrimaryRoleAndDamage() {
        assertEquals(BuildArchetype.TANK, champion(TANK, MAGE, attack = 5, magic = 7).archetype())
        assertEquals(BuildArchetype.SUPPORT, champion(SUPPORT, MAGE, attack = 4, magic = 7).archetype())
        assertEquals(BuildArchetype.MAGE, champion(MARKSMAN, MAGE, attack = 6, magic = 7).archetype())
        assertEquals(BuildArchetype.MARKSMAN, champion(MARKSMAN, attack = 9, magic = 1).archetype())
        assertEquals(BuildArchetype.ASSASSIN, champion(ASSASSIN, attack = 9, magic = 1).archetype())
        assertEquals(BuildArchetype.FIGHTER, champion(FIGHTER, TANK, attack = 8, magic = 2).archetype())
    }

    private fun champion(vararg roles: ChampionRole, attack: Int, magic: Int) = Champion(
        id = "Test",
        name = "Test",
        roles = roles.toList(),
        attack = attack,
        magic = magic,
    )
}
