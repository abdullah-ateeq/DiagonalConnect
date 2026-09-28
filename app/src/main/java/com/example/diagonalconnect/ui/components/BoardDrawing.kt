package com.example.diagonalconnect.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.theme.BoardPalette
import com.example.diagonalconnect.ui.theme.PlayerOneTone
import com.example.diagonalconnect.ui.theme.PlayerTwoTone
import com.example.diagonalconnect.ui.theme.tone

/** Fraction of the board side reserved on each edge for the objective rails. */
internal const val RAIL_FRACTION = 0.06f

/** Piece radius relative to a cell side. */
internal const val PIECE_RADIUS_FRACTION = 0.36f

internal fun cellCenter(position: GridPosition, cell: Float, origin: Offset): Offset =
    Offset(origin.x + (position.col + 0.5f) * cell, origin.y + (position.row + 0.5f) * cell)

/** Outer frame plus player-coloured rails: Player 1 top/bottom, Player 2 left/right. */
internal fun DrawScope.drawBoardFrame(
    palette: BoardPalette,
    inset: Float,
    playerOneRailAlpha: Float,
    playerTwoRailAlpha: Float,
) {
    val w = size.width
    drawRoundRect(color = palette.frame, cornerRadius = CornerRadius(w * 0.035f))
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.05f), Color.Transparent), endY = w * 0.5f),
        cornerRadius = CornerRadius(w * 0.035f),
    )
    drawRoundRect(
        color = palette.frameEdge.copy(alpha = 0.45f),
        topLeft = Offset(inset * 0.88f, inset * 0.88f),
        size = Size(w - inset * 1.76f, w - inset * 1.76f),
        cornerRadius = CornerRadius(inset * 0.3f),
        style = Stroke(width = w * 0.003f),
    )

    val thickness = inset * 0.3f
    val railCenter = inset * 0.46f
    val start = inset + w * 0.008f
    val length = w - 2 * start
    val p1 = Brush.horizontalGradient(listOf(PlayerOneTone.dark, PlayerOneTone.light, PlayerOneTone.dark), startX = start, endX = start + length)
    val p2 = Brush.verticalGradient(listOf(PlayerTwoTone.dark, PlayerTwoTone.light, PlayerTwoTone.dark), startY = start, endY = start + length)
    val radius = CornerRadius(thickness / 2)
    drawRoundRect(p1, Offset(start, railCenter - thickness / 2), Size(length, thickness), radius, alpha = playerOneRailAlpha)
    drawRoundRect(p1, Offset(start, w - railCenter - thickness / 2), Size(length, thickness), radius, alpha = playerOneRailAlpha)
    drawRoundRect(p2, Offset(railCenter - thickness / 2, start), Size(thickness, length), radius, alpha = playerTwoRailAlpha)
    drawRoundRect(p2, Offset(w - railCenter - thickness / 2, start), Size(thickness, length), radius, alpha = playerTwoRailAlpha)

    listOf(
        Offset(railCenter, railCenter), Offset(w - railCenter, railCenter),
        Offset(railCenter, w - railCenter), Offset(w - railCenter, w - railCenter),
    ).forEach { drawCircle(palette.frameEdge, radius = thickness * 0.42f, center = it) }
}

/** Recessed tiles with a faint two-tone parity so diagonal lanes read at a glance. */
internal fun DrawScope.drawTiles(palette: BoardPalette, gridSize: Int, origin: Offset, boardSide: Float) {
    val cell = boardSide / gridSize
    val gap = cell * 0.055f
    val tileSize = Size(cell - 2 * gap, cell - 2 * gap)
    val corner = CornerRadius(cell * 0.16f)
    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            val topLeft = Offset(origin.x + col * cell + gap, origin.y + row * cell + gap)
            drawRoundRect(palette.tileLip, topLeft + Offset(0f, cell * 0.028f), tileSize, corner)
            drawRoundRect(if ((row + col) % 2 == 0) palette.tileLight else palette.tileDark, topLeft, tileSize, corner)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(palette.tileShadow.copy(alpha = 0.55f), Color.Transparent),
                    startY = topLeft.y,
                    endY = topLeft.y + tileSize.height * 0.42f,
                ),
                topLeft = topLeft,
                size = tileSize,
                cornerRadius = corner,
            )
        }
    }
}

/** Draws connectors only between consecutive cells of [path], revealed progressively. */
internal fun DrawScope.drawPathConnectors(
    path: List<GridPosition>,
    cell: Float,
    origin: Offset,
    color: Color,
    progress: Float,
) {
    if (path.size < 2 || progress <= 0f) return
    val segments = path.size - 1
    val revealed = progress * segments
    for (i in 0 until segments) {
        val t = (revealed - i).coerceIn(0f, 1f)
        if (t <= 0f) break
        val a = cellCenter(path[i], cell, origin)
        val b = lerp(a, cellCenter(path[i + 1], cell, origin), t)
        drawLine(color.copy(alpha = 0.2f), a, b, strokeWidth = cell * 0.2f, cap = StrokeCap.Round)
        drawLine(color, a, b, strokeWidth = cell * 0.07f, cap = StrokeCap.Round)
    }
}

private fun pieceOutline(player: Player, center: Offset, radius: Float): Path = Path().apply {
    when (player) {
        Player.PLAYER_ONE -> {
            val r = radius * 1.12f
            moveTo(center.x, center.y - r)
            lineTo(center.x + r, center.y)
            lineTo(center.x, center.y + r)
            lineTo(center.x - r, center.y)
            close()
        }
        Player.PLAYER_TWO -> addOval(Rect(center, radius))
    }
}

/**
 * Raised, bevelled piece. Player 1 is a diamond with an engraved diamond, Player 2 a disc
 * with an engraved ring, so the players never rely on colour alone.
 */
internal fun DrawScope.drawPiece(
    player: Player,
    center: Offset,
    radius: Float,
    alpha: Float = 1f,
    brighten: Float = 0f,
) {
    if (alpha <= 0f) return
    val tone = player.tone
    val light = lerp(tone.light, Color.White, 0.2f * brighten)
    val base = lerp(tone.base, tone.light, 0.35f * brighten)
    val dark = lerp(tone.dark, tone.base, 0.3f * brighten)
    val diagonal = Offset(radius, radius)
    val outline = pieceOutline(player, center, radius)

    translate(top = radius * 0.14f) {
        drawPath(outline, Color.Black, alpha = 0.4f * alpha)
    }
    drawPath(
        outline,
        Brush.linearGradient(listOf(light, base, dark), start = center - diagonal, end = center + diagonal),
        alpha = alpha,
    )
    drawPath(
        pieceOutline(player, center, radius * 0.72f),
        Brush.linearGradient(
            listOf(dark.copy(alpha = 0.55f), base.copy(alpha = 0f), light.copy(alpha = 0.4f)),
            start = center - diagonal * 0.72f,
            end = center + diagonal * 0.72f,
        ),
        alpha = alpha,
    )
    drawPath(
        outline,
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), start = center - diagonal, end = center),
        alpha = alpha,
        style = Stroke(width = radius * 0.06f),
    )
    drawPath(
        pieceOutline(player, center, radius * 0.3f),
        tone.symbol,
        alpha = alpha * (0.8f + 0.2f * brighten),
        style = Stroke(width = radius * 0.1f),
    )
}
