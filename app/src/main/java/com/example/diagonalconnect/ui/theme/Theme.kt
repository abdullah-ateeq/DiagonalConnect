package com.example.diagonalconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.diagonalconnect.data.BoardTheme
import com.example.diagonalconnect.data.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Bronze,
    onPrimary = Color(0xFF1E1810),
    primaryContainer = Color(0xFF3A3023),
    onPrimaryContainer = Color(0xFFEBD9B8),
    secondary = Color(0xFF8C98A6),
    onSecondary = Color(0xFF121820),
    background = Graphite,
    onBackground = Color(0xFFE7E3DC),
    surface = GraphiteSurface,
    onSurface = Color(0xFFE7E3DC),
    surfaceVariant = GraphiteRaised,
    onSurfaceVariant = Color(0xFF9BA2AB),
    surfaceContainerLowest = Graphite,
    surfaceContainerLow = GraphiteSurface,
    surfaceContainer = GraphiteSurface,
    surfaceContainerHigh = GraphiteRaised,
    surfaceContainerHighest = GraphiteHigh,
    outline = Color(0xFF444B55),
    outlineVariant = Color(0xFF2B3139),
    error = Color(0xFFE08A7A),
)

private val LightColorScheme = lightColorScheme(
    primary = BronzeDeep,
    onPrimary = Color(0xFFFFFBF4),
    primaryContainer = Color(0xFFEADCC2),
    onPrimaryContainer = Color(0xFF3A2A10),
    secondary = Color(0xFF55606C),
    onSecondary = Color.White,
    background = Parchment,
    onBackground = Color(0xFF1A1D22),
    surface = ParchmentSurface,
    onSurface = Color(0xFF1A1D22),
    surfaceVariant = ParchmentRaised,
    onSurfaceVariant = Color(0xFF5A6068),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = ParchmentSurface,
    surfaceContainer = ParchmentSurface,
    surfaceContainerHigh = ParchmentRaised,
    surfaceContainerHighest = ParchmentHigh,
    outline = Color(0xFFB3ADA2),
    outlineVariant = Color(0xFFD6D1C7),
    error = Color(0xFFA0442F),
)

@Composable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

@Composable
fun DiagonalConnectTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    boardTheme: BoardTheme = BoardTheme.CLASSIC,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (themeMode.isDark()) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalBoardPalette provides boardPaletteFor(boardTheme)) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
