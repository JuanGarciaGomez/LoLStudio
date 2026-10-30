package com.project.lolstudio.staticdata.di

import com.project.lolstudio.staticdata.data.DataDragonRepository
import com.project.lolstudio.staticdata.domain.repository.StaticDataRepository
import com.project.lolstudio.staticdata.domain.usecase.GetChampionPortraitUrls
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val staticDataModule = module {
    singleOf(::DataDragonRepository) bind StaticDataRepository::class
    factoryOf(::GetChampionPortraitUrls)
}
