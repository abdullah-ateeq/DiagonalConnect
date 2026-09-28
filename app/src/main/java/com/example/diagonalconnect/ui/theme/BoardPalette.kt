package com.example.diagonalconnect.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.diagonalconnect.data.BoardTheme

@Immutable
data class BoardPalette(
    val frame: Color,
    val frameEdge: Color,
    val tileLight: Color,
    val tileDark: Color,
    val tileLip: Color,
    val tileShadow: Color,
    val lastMoveRing: Color,
)

val ClassicBoardPalette = BoardPalette(
    frame = Color(0xFF25282D),
    frameEdge = Color(0xFF6A5A41),
    tileLight = Color(0xFF41464D),
    tileDark = Color(0xFF383C42),
    tileLip = Color(0xFF52585F),
    tileShadow = Color(0xFF17191C),
    lastMoveRing = Color(0xFFE9D8B4),
)

val MidnightBoardPalette = BoardPalette(
    frame = Color(0xFF0B111A),
    frameEdge = Color(0xFF3D4B60),
    tileLight = Color(0xFF1E283A),
    tileDark = Color(0xFF182131),
    tileLip = Color(0xFF2A3852),
    tileShadow = Color(0xFF05080D),
    lastMoveRing = Color(0xFFD8E2F0),
)

fun boardPaletteFor(theme: BoardTheme): BoardPalette = when (theme) {
    BoardTheme.CLASSIC -> ClassicBoardPalette
    BoardTheme.MIDNIGHT -> MidnightBoardPalette
}

val LocalBoardPalette = staticCompositionLocalOf { ClassicBoardPalette }
