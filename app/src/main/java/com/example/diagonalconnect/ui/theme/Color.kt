package com.example.diagonalconnect.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.diagonalconnect.game.Player

val Bronze = Color(0xFFC2A06A)
val BronzeDeep = Color(0xFF8A6A36)

val Graphite = Color(0xFF101318)
val GraphiteSurface = Color(0xFF171B21)
val GraphiteRaised = Color(0xFF20252D)
val GraphiteHigh = Color(0xFF272D36)
val Parchment = Color(0xFFEFECE6)
val ParchmentSurface = Color(0xFFF7F5F1)
val ParchmentRaised = Color(0xFFE6E2DA)
val ParchmentHigh = Color(0xFFDCD7CD)

/** Three-stop tone used for raised, bevelled pieces. */
@Immutable
data class PieceTone(
    val light: Color,
    val base: Color,
    val dark: Color,
    val symbol: Color,
)

val PlayerOneTone = PieceTone(
    light = Color(0xFFE0976D),
    base = Color(0xFFB5583A),
    dark = Color(0xFF6E2A1F),
    symbol = Color(0xFFF6DCCB),
)

val PlayerTwoTone = PieceTone(
    light = Color(0xFF4FB3B0),
    base = Color(0xFF1D6F7A),
    dark = Color(0xFF0B3A4A),
    symbol = Color(0xFFCDEDEB),
)

val Player.tone: PieceTone
    get() = if (this == Player.PLAYER_ONE) PlayerOneTone else PlayerTwoTone

/** Readable accent for text and outlines on both light and dark surfaces. */
val Player.accent: Color
    get() = if (this == Player.PLAYER_ONE) Color(0xFFC4683F) else Color(0xFF2B8C93)
