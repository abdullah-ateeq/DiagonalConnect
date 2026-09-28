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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.components.MiniBoard
import com.example.diagonalconnect.ui.components.PlayerSymbol
import com.example.diagonalconnect.ui.components.ScreenHeader
import com.example.diagonalconnect.ui.components.SectionLabel
import com.example.diagonalconnect.ui.components.label
import com.example.diagonalconnect.ui.components.objective
import com.example.diagonalconnect.ui.theme.accent

private val Rules = listOf(
    "Players take turns placing one piece on any empty cell.",
    "Pieces never move after placement and are never removed.",
    "Pieces connect only diagonally — up-left, up-right, down-left, down-right.",
    "Pieces touching side-by-side or above-below do not connect.",
    "Block your opponent while extending your own diagonal network.",
    "The first player to complete a connected path wins. A full board with no path is a draw.",
)

private val DiagonalExample = listOf(GridPosition(0, 0), GridPosition(1, 1), GridPosition(2, 2))
private val OrthogonalExample = listOf(GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1))
private val SamplePath = listOf(GridPosition(0, 1), GridPosition(1, 2), GridPosition(2, 1), GridPosition(3, 2))

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        ScreenHeader("HOW TO PLAY", onBack)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                SectionLabel("THE DEFINING RULE")
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Diagram(
                        caption = "Diagonal neighbours connect",
                        modifier = Modifier.weight(1f),
                    ) {
                        MiniBoard(
                            gridSize = 3,
                            pieces = DiagonalExample.associateWith { Player.PLAYER_ONE },
                            path = DiagonalExample,
                            description = "Three pieces placed corner to corner are connected",
                        )
                    }
                    Diagram(
                        caption = "Side-by-side pieces do not",
                        modifier = Modifier.weight(1f),
                    ) {
                        MiniBoard(
                            gridSize = 3,
                            pieces = OrthogonalExample.associateWith { Player.PLAYER_ONE },
                            blockedLinks = OrthogonalExample.zipWithNext(),
                            description = "Three pieces stacked in a column are not connected",
                        )
                    }
                }

                SectionLabel("OBJECTIVES")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MiniBoard(
                        gridSize = 4,
                        pieces = SamplePath.associateWith { Player.PLAYER_ONE } +
                            mapOf(GridPosition(1, 0) to Player.PLAYER_TWO, GridPosition(2, 3) to Player.PLAYER_TWO),
                        path = SamplePath,
                        showRails = true,
                        description = "A bent Player 1 path joining the top and bottom rails",
                        modifier = Modifier.width(128.dp),
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ObjectiveRow(Player.PLAYER_ONE)
                        ObjectiveRow(Player.PLAYER_TWO)
                        Text(
                            "Coloured rails around the board mark each player's target edges. Paths may bend freely.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                SectionLabel("RULES")
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Rules.forEachIndexed { index, rule ->
                            Row {
                                Text(
                                    "%02d".format(index + 1),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.width(34.dp),
                                )
                                Text(rule, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun Diagram(caption: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.widthIn(max = 180.dp)) { content() }
        Spacer(Modifier.height(8.dp))
        Text(
            caption,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ObjectiveRow(player: Player) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PlayerSymbol(player, size = 26.dp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(player.label, style = MaterialTheme.typography.labelMedium, color = player.accent)
            Text(player.objective, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
