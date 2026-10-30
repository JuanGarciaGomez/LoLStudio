package com.project.lolstudio.recommendations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.project.lolstudio.recommendations.domain.model.BuildArchetype
import com.project.lolstudio.recommendations.domain.model.BuildRecommendation
import com.project.lolstudio.recommendations.domain.model.DamageType
import com.project.lolstudio.recommendations.domain.model.EnemyDamage
import com.project.lolstudio.staticdata.domain.model.ShopItem
import kotlin.math.roundToInt

@Composable
fun BuildRecommendationPanel(recommendation: BuildRecommendation?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Build recomendada", style = MaterialTheme.typography.titleLarge)
        if (recommendation == null) {
            Text("Recomendaciones no disponibles para esta partida.")
            return@Column
        }
        Notes(recommendation)
        ItemSection(title = "Botas", items = listOfNotNull(recommendation.boots))
        ItemSection(title = "Núcleo", items = recommendation.core)
        ItemSection(title = "Defensa", items = recommendation.defensive)
    }
}

@Composable
private fun Notes(recommendation: BuildRecommendation) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Perfil de tu campeón: ${recommendation.archetype.label()}")
        Text(recommendation.enemyDamage.summary())
        recommendation.enemyDamage.champions
            .groupBy({ it.type }, { it.championName })
            .forEach { (type, names) ->
                Text("${type.label()}: ${names.joinToString()}", style = MaterialTheme.typography.bodySmall)
            }
    }
}

@Composable
private fun ItemSection(title: String, items: List<ShopItem>) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        items.forEach { ItemRow(it) }
    }
}

@Composable
private fun ItemRow(item: ShopItem) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)),
        )
        Column {
            Text(item.name, style = MaterialTheme.typography.bodyMedium)
            Text("${item.totalGold} de oro", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun EnemyDamage.summary(): String {
    val magicPercent = (magicRatio * 100).roundToInt()
    return when (type) {
        DamageType.PHYSICAL -> "Enemigos mayormente físicos (${100 - magicPercent}% AD): prioriza armadura."
        DamageType.MAGIC -> "Enemigos mayormente mágicos ($magicPercent% AP): prioriza resistencia mágica."
        DamageType.MIXED -> "Daño enemigo mixto ($magicPercent% AP): combina armadura y resistencia mágica."
    }
}

private fun DamageType.label() = when (this) {
    DamageType.PHYSICAL -> "Físico"
    DamageType.MAGIC -> "Mágico"
    DamageType.MIXED -> "Mixto"
}

private fun BuildArchetype.label() = when (this) {
    BuildArchetype.MAGE -> "Mago"
    BuildArchetype.MARKSMAN -> "Tirador"
    BuildArchetype.ASSASSIN -> "Asesino"
    BuildArchetype.FIGHTER -> "Luchador"
    BuildArchetype.TANK -> "Tanque"
    BuildArchetype.SUPPORT -> "Soporte"
}
