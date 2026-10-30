package com.project.lolstudio.game.data.liveclient

import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.game.domain.repository.LiveGameRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

const val LIVE_CLIENT_HOST = "127.0.0.1"
private const val ALL_GAME_DATA_URL = "https://$LIVE_CLIENT_HOST:2999/liveclientdata/allgamedata"

class LiveClientGameRepository(private val httpClient: HttpClient) : LiveGameRepository {

    override suspend fun fetchCurrentGame(): LiveGame? =
        try {
            httpClient.get(ALL_GAME_DATA_URL).body<AllGameDataDto>().toDomain()
        } catch (e: IOException) {
            // The game only opens port 2999 while a match is running.
            null
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) null else throw e
        }
}
