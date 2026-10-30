package com.project.lolstudio.staticdata.data

import kotlinx.serialization.Serializable

@Serializable
data class ChampionListDto(val data: Map<String, ChampionDto>)

@Serializable
data class ChampionDto(
    val id: String,
    val name: String,
    val tags: List<String>,
    val info: ChampionInfoDto,
)

@Serializable
data class ChampionInfoDto(
    val attack: Int,
    val magic: Int,
)

@Serializable
data class ShopItemListDto(val data: Map<String, ShopItemDto>)

@Serializable
data class ShopItemDto(
    val name: String,
    val tags: List<String> = emptyList(),
    val gold: GoldDto,
    val image: ImageDto,
    val maps: Map<String, Boolean> = emptyMap(),
    val into: List<String> = emptyList(),
    val depth: Int? = null,
    val inStore: Boolean = true,
    val requiredChampion: String? = null,
    val requiredAlly: String? = null,
)

@Serializable
data class GoldDto(
    val total: Int,
    val purchasable: Boolean,
)

@Serializable
data class ImageDto(val full: String)
