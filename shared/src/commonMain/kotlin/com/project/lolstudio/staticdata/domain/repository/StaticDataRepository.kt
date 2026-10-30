package com.project.lolstudio.staticdata.domain.repository

interface StaticDataRepository {
    suspend fun championPortraitUrl(championId: String): String
}
