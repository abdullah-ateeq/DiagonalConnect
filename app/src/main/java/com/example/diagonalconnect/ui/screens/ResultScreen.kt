package com.example.diagonalconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.MatchSummary
import com.example.diagonalconnect.ui.components.DiagonalBoard
import com.example.diagonalconnect.ui.components.OrnamentDivider
import com.example.diagonalconnect.ui.components.PlayerSymbol
import com.example.diagonalconnect.ui.components.PrimaryActionButton
import com.example.diagonalconnect.ui.components.SecondaryActionButton
import com.example.diagonalconnect.ui.components.label
import com.example.diagonalconnect.ui.theme.accent

@Composable
fun ResultScreen(
    summary: MatchSummary?,
    showLastMove: Boolean,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (summary == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No finished match", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(24.dp))
                PrimaryActionButton("HOME", onHome, Modifier.widthIn(min = 200.dp))
            }
            return@BoxWithConstraints
        }
        val board = @Composable { modifier: Modifier ->
            DiagonalBoard(
                state = summary.finalState,
                modifier = modifier,
                interactive = false,
                showLastMove = showLastMove,
            )
        }
        if (maxWidth > maxHeight) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) { board(Modifier) }
                Spacer(Modifier.width(24.dp))
                Column(
                    Modifier
                        .widthIn(max = 380.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    ResultDetails(summary, onPlayAgain, onHome)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ResultHeadline(summary)
                Spacer(Modifier.height(20.dp))
                board(Modifier.widthIn(max = 280.dp).fillMaxWidth(0.75f))
                Spacer(Modifier.height(20.dp))
                ResultStats(summary)
                Spacer(Modifier.height(24.dp))
                ResultButtons(onPlayAgain, onHome)
            }
        }
    }
}

@Composable
private fun ResultDetails(summary: MatchSummary, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ResultHeadline(summary)
        Spacer(Modifier.height(20.dp))
        ResultStats(summary)
        Spacer(Modifier.height(20.dp))
        ResultButtons(onPlayAgain, onHome)
    }
}

@Composable
private fun ResultHeadline(summary: MatchSummary) {
    val winner = summary.winner
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (winner != null) {
            PlayerSymbol(winner, size = 44.dp)
        } else {
            Row {
                PlayerSymbol(Player.PLAYER_ONE, size = 34.dp)
                Spacer(Modifier.width(6.dp))
                PlayerSymbol(Player.PLAYER_TWO, size = 34.dp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = winner?.let { "${it.label} WINS" } ?: "DRAW",
            style = MaterialTheme.typography.headlineMedium,
            color = winner?.accent ?: MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(10.dp))
        OrnamentDivider()
        Spacer(Modifier.height(10.dp))
        Text(
            text = when (winner) {
                Player.PLAYER_ONE -> "Top and bottom joined by an unbroken diagonal path."
                Player.PLAYER_TWO -> "Left and right joined by an unbroken diagonal path."
                null -> "The board is full and neither path was completed."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ResultStats(summary: MatchSummary) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
            StatRow("Moves Played", summary.moves.toString())
            StatDivider()
            StatRow("Winning Path Length", if (summary.pathLength > 0) "${summary.pathLength} pieces" else "—")
            StatDivider()
            StatRow("Player 1 Pieces", summary.playerOnePieces.toString())
            StatDivider()
            StatRow("Player 2 Pieces", summary.playerTwoPieces.toString())
            StatDivider()
            StatRow("Match Duration", formatDuration(summary.durationMillis))
        }
    }
}

@Composable
private fun StatDivider() = HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ResultButtons(onPlayAgain: () -> Unit, onHome: () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PrimaryActionButton("PLAY AGAIN", onPlayAgain, Modifier.fillMaxWidth())
        SecondaryActionButton("HOME", onHome, Modifier.fillMaxWidth())
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
