package com.example.diagonalconnect.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.diagonalconnect.game.BOARD_SIZE
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.components.OrnamentDivider
import com.example.diagonalconnect.ui.components.PIECE_RADIUS_FRACTION
import com.example.diagonalconnect.ui.components.RAIL_FRACTION
import com.example.diagonalconnect.ui.components.cellCenter
import com.example.diagonalconnect.ui.components.drawBoardFrame
import com.example.diagonalconnect.ui.components.drawPathConnectors
import com.example.diagonalconnect.ui.components.drawPiece
import com.example.diagonalconnect.ui.components.drawTiles
import com.example.diagonalconnect.ui.theme.LocalBoardPalette
import com.example.diagonalconnect.ui.theme.PlayerOneTone

private const val SPLASH_DURATION_MS = 1_500

private val SplashPath = listOf(
    GridPosition(0, 2), GridPosition(1, 3), GridPosition(2, 2), GridPosition(3, 3),
    GridPosition(4, 4), GridPosition(5, 5), GridPosition(6, 4), GridPosition(7, 5),
)

/** Placement order: Player 1 builds the route while Player 2 places a few blockers. */
private val SplashSequence: List<Pair<GridPosition, Player>> = listOf(
    SplashPath[0] to Player.PLAYER_ONE,
    GridPosition(1, 5) to Player.PLAYER_TWO,
    SplashPath[1] to Player.PLAYER_ONE,
    GridPosition(3, 1) to Player.PLAYER_TWO,
    SplashPath[2] to Player.PLAYER_ONE,
    GridPosition(5, 3) to Player.PLAYER_TWO,
    SplashPath[3] to Player.PLAYER_ONE,
    GridPosition(4, 6) to Player.PLAYER_TWO,
    SplashPath[4] to Player.PLAYER_ONE,
    SplashPath[5] to Player.PLAYER_ONE,
    SplashPath[6] to Player.PLAYER_ONE,
    SplashPath[7] to Player.PLAYER_ONE,
)

private fun window(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val finish by rememberUpdatedState(onFinished)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(SPLASH_DURATION_MS, easing = LinearEasing))
        finish()
    }
    val palette = LocalBoardPalette.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .semantics { contentDescription = "Diagonal Connect" },
        contentAlignment = Alignment.Center,
    ) {
        val boardSize = min(min(maxWidth, maxHeight) * 0.62f, 320.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Canvas(
                Modifier
                    .size(boardSize)
                    .graphicsLayer { alpha = window(progress.value, 0f, 0.18f) },
            ) {
                val t = progress.value
                val inset = size.width * RAIL_FRACTION
                val complete = window(t, 0.64f, 0.82f)
                drawBoardFrame(palette, inset, 0.45f + 0.5f * complete, 0.3f * (1f - complete) + 0.12f)
                val inner = size.width - inset * 2
                val origin = Offset(inset, inset)
                val cell = inner / BOARD_SIZE
                drawTiles(palette, BOARD_SIZE, origin, inner)
                drawPathConnectors(SplashPath, cell, origin, PlayerOneTone.light, complete)
                SplashSequence.forEachIndexed { index, (position, player) ->
                    val start = 0.14f + index * 0.04f
                    val appear = window(t, start, start + 0.07f)
                    if (appear > 0f) {
                        val center = cellCenter(position, cell, origin)
                        val onPath = player == Player.PLAYER_ONE
                        val alpha = appear * if (onPath) 1f else 1f - 0.6f * complete
                        scale(0.8f + 0.2f * appear, center) {
                            drawPiece(player, center, cell * PIECE_RADIUS_FRACTION, alpha, if (onPath) complete else 0f)
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
            val titleAlpha = window(progress.value, 0.66f, 0.88f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha
                    translationY = (1f - titleAlpha) * 24f
                },
            ) {
                Text(
                    text = "DIAGONAL CONNECT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                OrnamentDivider()
            }
        }
    }
}
