package com.example.diagonalconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.data.GameStats
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.components.MiniBoard
import com.example.diagonalconnect.ui.components.OrnamentDivider
import com.example.diagonalconnect.ui.components.PrimaryActionButton
import com.example.diagonalconnect.ui.components.StatBlock
import com.example.diagonalconnect.ui.theme.accent

private val EmblemPath = listOf(GridPosition(0, 0), GridPosition(1, 1), GridPosition(2, 2))

@Composable
fun HomeScreen(
    stats: GameStats,
    onPlay: () -> Unit,
    onHowToPlay: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MiniBoard(
                gridSize = 3,
                pieces = EmblemPath.associateWith { Player.PLAYER_ONE } + (GridPosition(0, 2) to Player.PLAYER_TWO),
                path = EmblemPath,
                description = "Emblem: three diamond pieces joined diagonally",
                modifier = Modifier.size(88.dp),
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "DIAGONAL\nCONNECT",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            OrnamentDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Think diagonally. Build the path.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(40.dp))
            PrimaryActionButton(text = "PLAY", onClick = onPlay, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MenuLink("HOW TO PLAY", onHowToPlay)
                MenuLink("SETTINGS", onSettings)
                MenuLink("ABOUT", onAbout)
            }
            Spacer(Modifier.height(32.dp))
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 18.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatBlock(stats.gamesPlayed.toString(), "GAMES", Modifier.weight(1f))
                    StatBlock(stats.playerOneWins.toString(), "P1 WINS", Modifier.weight(1f), Player.PLAYER_ONE.accent)
                    StatBlock(stats.playerTwoWins.toString(), "P2 WINS", Modifier.weight(1f), Player.PLAYER_TWO.accent)
                }
            }
        }
    }
}

@Composable
private fun MenuLink(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
