package com.example.diagonalconnect.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.diagonalconnect.game.BOARD_SIZE
import com.example.diagonalconnect.game.DiagonalConnectState
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.PLACEMENT_ANIMATION_MS
import com.example.diagonalconnect.ui.theme.LocalBoardPalette
import com.example.diagonalconnect.ui.theme.tone

@Composable
fun DiagonalBoard(
    state: DiagonalConnectState,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    showLastMove: Boolean = true,
    highlightEdges: Boolean = true,
    onCellTap: (GridPosition) -> Unit = {},
) {
    val palette = LocalBoardPalette.current
    val winner = state.winner
    val pathSet = remember(state.winningPath) { state.winningPath.toSet() }

    val connectorProgress = remember { Animatable(if (state.winningPath.isNotEmpty()) 1f else 0f) }
    LaunchedEffect(state.winningPath) {
        if (state.winningPath.isEmpty()) {
            connectorProgress.snapTo(0f)
        } else if (connectorProgress.value < 1f) {
            connectorProgress.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }
    }

    val pulse = if (winner != null) {
        val transition = rememberInfiniteTransition(label = "edgePulse")
        transition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "edgePulseAlpha",
        ).value
    } else {
        1f
    }

    fun railAlpha(player: Player): Float = when {
        winner == player -> pulse
        winner != null -> 0.15f
        !highlightEdges -> 0.28f
        state.isDraw -> 0.4f
        player == state.currentPlayer -> 0.95f
        else -> 0.55f
    }

    val cellsEnabled = interactive && !state.isGameOver && !state.isAnimating

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .semantics {
                contentDescription = "Game board, 8 by 8. Player 1 connects top to bottom. " +
                    "Player 2 connects left to right."
            },
    ) {
        val inset = maxWidth * RAIL_FRACTION
        val p1Rail = railAlpha(Player.PLAYER_ONE)
        val p2Rail = railAlpha(Player.PLAYER_TWO)
        Canvas(Modifier.fillMaxSize()) {
            drawBoardFrame(palette, inset.toPx(), p1Rail, p2Rail)
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(inset),
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawTiles(palette, BOARD_SIZE, Offset.Zero, size.width)
                if (winner != null) {
                    drawPathConnectors(
                        path = state.winningPath,
                        cell = size.width / BOARD_SIZE,
                        origin = Offset.Zero,
                        color = winner.tone.light,
                        progress = connectorProgress.value,
                    )
                }
            }
            Column(Modifier.fillMaxSize()) {
                for (row in 0 until BOARD_SIZE) {
                    Row(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        for (col in 0 until BOARD_SIZE) {
                            val position = GridPosition(row, col)
                            val owner = state.board[row][col]
                            BoardCell(
                                position = position,
                                owner = owner,
                                isLastMove = showLastMove && state.lastMove == position,
                                inWinningPath = position in pathSet,
                                dimmed = winner != null && owner != null && position !in pathSet,
                                enabled = cellsEnabled && owner == null,
                                onTap = onCellTap,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    position: GridPosition,
    owner: Player?,
    isLastMove: Boolean,
    inWinningPath: Boolean,
    dimmed: Boolean,
    enabled: Boolean,
    onTap: (GridPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBoardPalette.current
    val appear = remember { Animatable(if (owner != null) 1f else 0f) }
    LaunchedEffect(owner) {
        if (owner == null) {
            appear.snapTo(0f)
        } else if (appear.value < 1f) {
            appear.animateTo(1f, tween(PLACEMENT_ANIMATION_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }
    val dim by animateFloatAsState(if (dimmed) 0.38f else 1f, tween(450), label = "dim")
    val glow by animateFloatAsState(if (inWinningPath) 1f else 0f, tween(450), label = "glow")

    val location = "row ${position.row + 1} column ${position.col + 1}"
    val description = when {
        owner == null -> "Empty cell, $location"
        inWinningPath -> "Winning path piece, Player ${owner.number}, $location"
        else -> "Player ${owner.number} piece $location" + if (isLastMove) ", last move" else ""
    }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) { contentDescription = description }
            .clickable(
                enabled = enabled,
                onClickLabel = "Place piece",
                role = Role.Button,
                interactionSource = null,
                indication = null,
            ) { onTap(position) }
            .drawBehind {
                if (owner == null) return@drawBehind
                val progress = appear.value
                val radius = size.minDimension * PIECE_RADIUS_FRACTION
                scale(0.8f + 0.2f * progress) {
                    drawPiece(owner, center, radius, alpha = progress * dim, brighten = glow)
                }
                if (isLastMove) {
                    drawCircle(
                        color = palette.lastMoveRing,
                        radius = radius * 1.28f,
                        center = center,
                        alpha = 0.75f * progress * dim,
                        style = Stroke(width = radius * 0.08f),
                    )
                }
            },
    )
}
