package com.example.diagonalconnect.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.theme.LocalBoardPalette
import com.example.diagonalconnect.ui.theme.PlayerOneTone
import com.example.diagonalconnect.ui.theme.PlayerTwoTone
import com.example.diagonalconnect.ui.theme.tone

/** Small static diagram board used on Home, How to Play and About. */
@Composable
fun MiniBoard(
    gridSize: Int,
    pieces: Map<GridPosition, Player>,
    description: String,
    modifier: Modifier = Modifier,
    path: List<GridPosition> = emptyList(),
    pathOwner: Player = Player.PLAYER_ONE,
    blockedLinks: List<Pair<GridPosition, GridPosition>> = emptyList(),
    showRails: Boolean = false,
) {
    val palette = LocalBoardPalette.current
    Canvas(
        modifier
            .aspectRatio(1f)
            .semantics { contentDescription = description },
    ) {
        val w = size.width
        val pad = if (showRails) w * 0.1f else w * 0.06f
        drawRoundRect(palette.frame, cornerRadius = CornerRadius(w * 0.06f))
        if (showRails) {
            val t = pad * 0.32f
            val c = pad * 0.48f
            val len = w - pad * 2
            drawRoundRect(PlayerOneTone.base, Offset(pad, c - t / 2), Size(len, t), CornerRadius(t / 2))
            drawRoundRect(PlayerOneTone.base, Offset(pad, w - c - t / 2), Size(len, t), CornerRadius(t / 2))
            drawRoundRect(PlayerTwoTone.base, Offset(c - t / 2, pad), Size(t, len), CornerRadius(t / 2))
            drawRoundRect(PlayerTwoTone.base, Offset(w - c - t / 2, pad), Size(t, len), CornerRadius(t / 2))
        }
        val inner = w - pad * 2
        val origin = Offset(pad, pad)
        val cell = inner / gridSize
        drawTiles(palette, gridSize, origin, inner)
        drawPathConnectors(path, cell, origin, pathOwner.tone.light, 1f)
        pieces.forEach { (position, player) ->
            drawPiece(player, cellCenter(position, cell, origin), cell * PIECE_RADIUS_FRACTION)
        }
        blockedLinks.forEach { (a, b) ->
            val mid = (cellCenter(a, cell, origin) + cellCenter(b, cell, origin)) / 2f
            val r = cell * 0.15f
            drawCircle(palette.frame, radius = r * 1.35f, center = mid)
            val cross = Color(0xFFE9D8B4)
            val stroke = cell * 0.05f
            drawLine(cross, mid + Offset(-r * 0.6f, -r * 0.6f), mid + Offset(r * 0.6f, r * 0.6f), stroke, StrokeCap.Round)
            drawLine(cross, mid + Offset(r * 0.6f, -r * 0.6f), mid + Offset(-r * 0.6f, r * 0.6f), stroke, StrokeCap.Round)
        }
    }
}
