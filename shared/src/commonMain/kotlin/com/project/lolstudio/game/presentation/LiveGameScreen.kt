package com.project.lolstudio.game.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.project.lolstudio.game.domain.model.LiveGame
import com.project.lolstudio.game.domain.model.Player
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration

@Composable
fun LiveGameRoute(viewModel: LiveGameViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LiveGameScreen(state)
}

@Composable
fun LiveGameScreen(state: LiveGameState) {
    Box(
        modifier = Modifier.fillMaxSize().safeContentPadding().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            LiveGameState.Loading -> CircularProgressIndicator()
            LiveGameState.WaitingForGame -> Text("Esperando una partida en curso…")
            is LiveGameState.Error -> Text("No se pudo leer la partida: ${state.message}")
            is LiveGameState.InGame -> GameOverview(state.game, state.championPortraitUrls)
        }
    }
}

@Composable
private fun GameOverview(game: LiveGame, portraitUrls: Map<String, String>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("${game.mode} · ${game.elapsed.asClock()}", style = MaterialTheme.typography.titleLarge)
        TeamSection(title = "Tú", players = listOf(game.me), portraitUrls = portraitUrls)
        TeamSection(title = "Aliados", players = game.allies, portraitUrls = portraitUrls)
        TeamSection(title = "Enemigos", players = game.enemies, portraitUrls = portraitUrls)
    }
}

@Composable
private fun TeamSection(title: String, players: List<Player>, portraitUrls: Map<String, String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        players.forEach { PlayerCard(it, portraitUrls[it.championId]) }
    }
}

@Composable
private fun PlayerCard(player: Player, portraitUrl: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = portraitUrl,
                contentDescription = player.championName,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
            )
            Column {
                Text(player.championName, style = MaterialTheme.typography.titleSmall)
                Text(player.riotId, style = MaterialTheme.typography.bodySmall)
                player.summary()?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

private fun Player.summary(): String? {
    val levelText = level?.let { "Nivel $it" }
    val scoreText = score?.let { "${it.kills}/${it.deaths}/${it.assists} · ${it.creepScore} CS" }
    return listOfNotNull(levelText, scoreText).joinToString(" · ").ifEmpty { null }
}

private fun Duration.asClock(): String = toComponents { minutes, seconds, _ ->
    "$minutes:${seconds.toString().padStart(2, '0')}"
}
