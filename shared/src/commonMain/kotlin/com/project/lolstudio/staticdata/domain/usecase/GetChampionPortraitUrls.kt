package com.project.lolstudio.staticdata.domain.usecase

import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import kotlin.coroutines.cancellation.CancellationException

class GetChampionPortraitUrls(private val repository: StaticDataRepository) {

    suspend operator fun invoke(championIds: Collection<String>): Map<String, String> =
        try {
            championIds.associateWith { repository.championPortraitUrl(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyMap()
        }
}
