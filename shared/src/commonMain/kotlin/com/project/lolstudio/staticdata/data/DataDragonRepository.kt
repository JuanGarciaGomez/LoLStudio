package com.project.lolstudio.staticdata.data

import com.project.lolstudio.core.util.SuspendLazy
import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ShopItem
import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com"
private const val VERSIONS_URL = "$DATA_DRAGON_URL/api/versions.json"
private const val LOCALE = "es_MX"

class DataDragonRepository(private val httpClient: HttpClient) : StaticDataRepository {

    private val latestVersion = SuspendLazy {
        httpClient.get(VERSIONS_URL).body<List<String>>().first()
    }

    private val cachedChampions = SuspendLazy {
        httpClient.get(dataUrl("champion.json")).body<ChampionListDto>().toDomain()
    }

    private val cachedItems = SuspendLazy {
        val cdnUrl = cdnUrl()
        httpClient.get(dataUrl("item.json")).body<ShopItemListDto>()
            .toRecommendableItems { fileName -> "$cdnUrl/img/item/$fileName" }
    }

    override suspend fun championPortraitUrl(championId: String): String =
        "${cdnUrl()}/img/champion/$championId.png"

    override suspend fun champions(): List<Champion> = cachedChampions.get()

    override suspend fun recommendableItems(): List<ShopItem> = cachedItems.get()

    private suspend fun cdnUrl() = "$DATA_DRAGON_URL/cdn/${latestVersion.get()}"

    private suspend fun dataUrl(fileName: String) = "${cdnUrl()}/data/$LOCALE/$fileName"
}
