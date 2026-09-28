package com.example.diagonalconnect.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diagonalconnect.data.AppSettings
import com.example.diagonalconnect.game.DiagonalConnectState
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.GameFeedback
import com.example.diagonalconnect.ui.GameUiState
import com.example.diagonalconnect.ui.GameViewModel
import com.example.diagonalconnect.ui.MatchBanner
import com.example.diagonalconnect.ui.components.DiagonalBoard
import com.example.diagonalconnect.ui.components.PlayerSymbol
import com.example.diagonalconnect.ui.components.SecondaryActionButton
import com.example.diagonalconnect.ui.components.label
import com.example.diagonalconnect.ui.components.objective
import com.example.diagonalconnect.ui.components.objectiveSpoken
import com.example.diagonalconnect.ui.theme.accent

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    settings: AppSettings,
    onMenu: () -> Unit,
    onShowResult: () -> Unit,
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val hapticsEnabled by rememberUpdatedState(settings.hapticsEnabled)
    val showResult by rememberUpdatedState(onShowResult)
    var confirmRestart by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.feedback.collect { if (hapticsEnabled) view.performGameHaptic(it) }
    }
    LaunchedEffect(ui.resultReady) {
        if (ui.resultReady) {
            viewModel.onResultShown()
            showResult()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(16.dp),
    ) {
        val controls = @Composable { modifier: Modifier ->
            GameControls(
                ui = ui,
                onMenu = onMenu,
                onUndo = viewModel::undo,
                onRestart = { confirmRestart = true },
                modifier = modifier,
            )
        }
        val board = @Composable { modifier: Modifier ->
            BoardWithBanner(ui, settings, onCellTap = viewModel::onCellTapped, modifier = modifier)
        }

        if (maxWidth > maxHeight) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    board(Modifier)
                }
                Spacer(Modifier.width(20.dp))
                Column(
                    modifier = Modifier
                        .widthIn(min = 260.dp, max = 360.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                ) {
                    TurnIndicator(ui.game, Modifier.fillMaxWidth())
                    PlayerPanel(Player.PLAYER_TWO, ui.game)
                    PlayerPanel(Player.PLAYER_ONE, ui.game)
                    controls(Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TurnIndicator(ui.game, Modifier.fillMaxWidth())
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PlayerPanel(Player.PLAYER_TWO, ui.game)
                    Box(
                        Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        board(Modifier)
                    }
                    PlayerPanel(Player.PLAYER_ONE, ui.game)
                }
                controls(Modifier.fillMaxWidth())
            }
        }
    }

    if (confirmRestart) {
        AlertDialog(
            onDismissRequest = { confirmRestart = false },
            title = { Text("Restart this match?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("All pieces will be cleared and Player 1 will move first.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmRestart = false
                    viewModel.newMatch()
                }) { Text("RESTART", style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = {
                TextButton(onClick = { confirmRestart = false }) {
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(20.dp),
        )
    }
}

@Composable
private fun BoardWithBanner(
    ui: GameUiState,
    settings: AppSettings,
    onCellTap: (com.example.diagonalconnect.game.GridPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        DiagonalBoard(
            state = ui.game,
            showLastMove = settings.showLastMove,
            highlightEdges = settings.highlightEdges,
            onCellTap = onCellTap,
        )
        AnimatedVisibility(
            visible = ui.banner != null,
            enter = fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.92f),
            exit = fadeOut(tween(150)),
        ) {
            val winner = ui.game.winner
            val color = winner?.accent ?: MaterialTheme.colorScheme.primary
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, color),
                shadowElevation = 8.dp,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
            ) {
                Text(
                    text = if (ui.banner == MatchBanner.DRAW) "BOARD FULL · DRAW" else "PATH COMPLETE",
                    style = MaterialTheme.typography.titleSmall,
                    color = color,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun TurnIndicator(state: DiagonalConnectState, modifier: Modifier = Modifier) {
    val player = state.winner ?: state.currentPlayer
    val headline = when {
        state.winner != null -> "${player.label} COMPLETES THE PATH"
        state.isDraw -> "DRAW"
        else -> "${player.label}'S TURN"
    }
    val detail = when {
        state.isDraw -> "No diagonal path was completed"
        else -> player.objective
    }
    val color = if (state.isDraw) MaterialTheme.colorScheme.onSurfaceVariant else player.accent
    Column(
        modifier = modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(headline, style = MaterialTheme.typography.titleSmall, color = color, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = "$detail  ·  Move ${state.moveCount}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerPanel(player: Player, state: DiagonalConnectState) {
    val active = !state.isGameOver && state.currentPlayer == player
    val isWinner = state.winner == player
    val emphasis by animateFloatAsState(
        targetValue = if (active || isWinner) 1f else 0f,
        animationSpec = tween(220),
        label = "panelEmphasis",
    )
    val pieces = state.pieceCount(player)
    val status = when {
        isWinner -> "WINNER"
        active -> "TO MOVE"
        else -> ""
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            if (active || isWinner) player.accent.copy(alpha = 0.4f + 0.5f * emphasis) else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Player ${player.number} ${player.objectiveSpoken}, $pieces pieces" +
                    when {
                        isWinner -> ", winner"
                        active -> ", current turn"
                        else -> ""
                    }
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerSymbol(player, size = 30.dp, modifier = Modifier.alpha(0.55f + 0.45f * emphasis))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(player.label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    if (status.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall,
                            color = player.accent,
                            modifier = Modifier
                                .background(player.accent.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text(player.objective, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(pieces.toString(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text("PIECES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun GameControls(
    ui: GameUiState,
    onMenu: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val over = ui.game.isGameOver
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryActionButton("MENU", onMenu, Modifier.weight(1f), height = 48.dp)
        SecondaryActionButton("UNDO", onUndo, Modifier.weight(1f), enabled = ui.canUndo && !over, height = 48.dp)
        SecondaryActionButton(
            "RESTART",
            onRestart,
            Modifier.weight(1f),
            enabled = !over && ui.game.moveCount > 0,
            height = 48.dp,
        )
    }
}

private fun View.performGameHaptic(feedback: GameFeedback) {
    val constant = when (feedback) {
        GameFeedback.PLACED -> HapticFeedbackConstants.CLOCK_TICK
        GameFeedback.PATH_COMPLETE ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.LONG_PRESS
        GameFeedback.DRAW -> HapticFeedbackConstants.VIRTUAL_KEY
    }
    performHapticFeedback(constant)
}
