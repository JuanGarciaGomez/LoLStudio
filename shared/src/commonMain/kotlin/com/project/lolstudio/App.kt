package com.project.lolstudio

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.project.lolstudio.core.di.platformModule
import com.project.lolstudio.game.di.gameModule
import com.project.lolstudio.game.presentation.LiveGameRoute
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(koinConfiguration { modules(platformModule, gameModule) }) {
        MaterialTheme {
            Surface {
                LiveGameRoute()
            }
        }
    }
}
