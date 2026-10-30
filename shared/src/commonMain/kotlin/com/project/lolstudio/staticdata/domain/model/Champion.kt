package com.project.lolstudio.staticdata.domain.model

data class Champion(
    val id: String,
    val name: String,
    val roles: List<ChampionRole>,
    val attack: Int,
    val magic: Int,
) {
    val primaryRole: ChampionRole? get() = roles.firstOrNull()
}

enum class ChampionRole { ASSASSIN, FIGHTER, MAGE, MARKSMAN, SUPPORT, TANK }
