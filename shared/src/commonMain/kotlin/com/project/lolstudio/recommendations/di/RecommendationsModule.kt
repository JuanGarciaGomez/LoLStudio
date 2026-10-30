package com.project.lolstudio.recommendations.di

import com.project.lolstudio.recommendations.domain.usecase.ClassifyEnemyDamage
import com.project.lolstudio.recommendations.domain.usecase.RecommendBuild
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val recommendationsModule = module {
    factoryOf(::ClassifyEnemyDamage)
    factoryOf(::RecommendBuild)
}
