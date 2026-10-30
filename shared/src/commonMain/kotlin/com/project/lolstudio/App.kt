package com.project.lolstudio

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.project.lolstudio.core.di.coreModule
import com.project.lolstudio.core.di.platformModule
import com.project.lolstudio.game.di.gameModule
import com.project.lolstudio.game.presentation.LiveGameRoute
import com.project.lolstudio.recommendations.di.recommendationsModule
import com.project.lolstudio.staticdata.di.staticDataModule
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(koinConfiguration { modules(coreModule, platformModule, staticDataModule, recommendationsModule, gameModule) }) {
        MaterialTheme {
            Surface {
                LiveGameRoute()
            }
        }
    }
}
