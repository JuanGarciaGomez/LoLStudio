package com.project.lolstudio.staticdata.data

import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com"
private const val VERSIONS_URL = "$DATA_DRAGON_URL/api/versions.json"

class DataDragonRepository(private val httpClient: HttpClient) : StaticDataRepository {

    private val versionMutex = Mutex()
    private var cachedVersion: String? = null

    override suspend fun championPortraitUrl(championId: String): String =
        "$DATA_DRAGON_URL/cdn/${latestVersion()}/img/champion/$championId.png"

    private suspend fun latestVersion(): String = versionMutex.withLock {
        cachedVersion ?: httpClient.get(VERSIONS_URL).body<List<String>>().first()
            .also { cachedVersion = it }
    }
}
