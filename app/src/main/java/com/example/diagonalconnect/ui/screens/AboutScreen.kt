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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.components.MiniBoard
import com.example.diagonalconnect.ui.components.OrnamentDivider
import com.example.diagonalconnect.ui.components.ScreenHeader

private val Features = listOf(
    "Local 2 Player" to "Two players, one device",
    "8×8 Grid" to "64 cells, one piece per cell",
    "Diagonal-Only Connections" to "Corners connect, sides never do",
    "Path Strategy" to "Build your route, block theirs",
    "Offline Play" to "No account or connection needed",
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        ScreenHeader("ABOUT", onBack)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val emblem = listOf(GridPosition(0, 0), GridPosition(1, 1), GridPosition(2, 2))
                MiniBoard(
                    gridSize = 3,
                    pieces = emblem.associateWith { Player.PLAYER_ONE } + (GridPosition(2, 0) to Player.PLAYER_TWO),
                    path = emblem,
                    description = "Diagonal Connect emblem",
                    modifier = Modifier.size(80.dp),
                )
                Spacer(Modifier.height(20.dp))
                Text("Diagonal Connect", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(4.dp))
                Text("Version 1.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(14.dp))
                OrnamentDivider()
                Spacer(Modifier.height(14.dp))
                Text(
                    "A polished local two-player strategy game where players build continuous diagonal " +
                        "networks to connect opposite sides of the board.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(28.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                        Features.forEachIndexed { index, (title, detail) ->
                            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                DiamondBullet()
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DiamondBullet() {
    val color = MaterialTheme.colorScheme.primary
    androidx.compose.foundation.Canvas(Modifier.size(10.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2, 0f)
            lineTo(size.width, size.height / 2)
            lineTo(size.width / 2, size.height)
            lineTo(0f, size.height / 2)
            close()
        }
        drawPath(path, color)
    }
}
