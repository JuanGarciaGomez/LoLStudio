package com.project.lolstudio.staticdata.domain.usecase

import com.project.lolstudio.staticdata.domain.model.Champion
import com.project.lolstudio.staticdata.domain.model.ShopItem
import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetChampionPortraitUrlsTest {

    @Test
    fun mapsEachChampionToItsPortraitUrl() = runTest {
        val getUrls = GetChampionPortraitUrls(FakeStaticDataRepository { "https://cdn/$it.png" })

        val urls = getUrls(listOf("Ahri", "Zed"))

        assertEquals(mapOf("Ahri" to "https://cdn/Ahri.png", "Zed" to "https://cdn/Zed.png"), urls)
    }

    @Test
    fun returnsNoUrlsWhenStaticDataIsUnavailable() = runTest {
        val getUrls = GetChampionPortraitUrls(FakeStaticDataRepository { error("offline") })

        assertEquals(emptyMap(), getUrls(listOf("Ahri")))
    }
}

private class FakeStaticDataRepository(private val portraitUrl: (String) -> String) : StaticDataRepository {
    override suspend fun championPortraitUrl(championId: String): String = portraitUrl(championId)
    override suspend fun champions(): List<Champion> = emptyList()
    override suspend fun recommendableItems(): List<ShopItem> = emptyList()
}
