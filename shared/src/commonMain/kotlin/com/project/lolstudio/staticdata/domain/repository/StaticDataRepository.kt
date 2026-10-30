package com.project.lolstudio.staticdata.domain.repository

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ShopItem

interface StaticDataRepository {
    suspend fun championPortraitUrl(championId: String): String
    suspend fun champions(): List<Champion>
    suspend fun recommendableItems(): List<ShopItem>
}
