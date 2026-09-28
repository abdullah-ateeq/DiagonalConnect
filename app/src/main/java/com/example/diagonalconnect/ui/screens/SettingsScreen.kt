package com.example.diagonalconnect.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.data.AppSettings
import com.example.diagonalconnect.data.BoardTheme
import com.example.diagonalconnect.data.GameStats
import com.example.diagonalconnect.data.ThemeMode
import com.example.diagonalconnect.game.Player
import com.example.diagonalconnect.ui.SettingsViewModel
import com.example.diagonalconnect.ui.components.ScreenHeader
import com.example.diagonalconnect.ui.components.SectionLabel
import com.example.diagonalconnect.ui.components.StatBlock
import com.example.diagonalconnect.ui.theme.accent

@Composable
fun SettingsScreen(
    settings: AppSettings,
    stats: GameStats,
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        ScreenHeader("SETTINGS", onBack)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SectionLabel("APPEARANCE")
                SettingsCard {
                    ChoiceRow(
                        title = "Theme",
                        options = listOf(ThemeMode.LIGHT to "Light", ThemeMode.DARK to "Dark", ThemeMode.SYSTEM to "System"),
                        selected = settings.themeMode,
                        onSelect = viewModel::setThemeMode,
                    )
                    CardDivider()
                    ChoiceRow(
                        title = "Board Theme",
                        options = listOf(BoardTheme.CLASSIC to "Classic", BoardTheme.MIDNIGHT to "Midnight"),
                        selected = settings.boardTheme,
                        onSelect = viewModel::setBoardTheme,
                    )
                }

                SectionLabel("GAMEPLAY", Modifier.padding(top = 8.dp))
                SettingsCard {
                    ToggleRow("Show Last Move", "Ring the most recent placement", settings.showLastMove, viewModel::setShowLastMove)
                    CardDivider()
                    ToggleRow("Highlight Target Edges", "Emphasise the current player's rails", settings.highlightEdges, viewModel::setHighlightEdges)
                }

                SectionLabel("FEEDBACK", Modifier.padding(top = 8.dp))
                SettingsCard {
                    ToggleRow("Sound Effects", "Placement, turn and result tones", settings.soundEnabled, viewModel::setSoundEnabled)
                    CardDivider()
                    ToggleRow("Haptic Feedback", "Subtle vibration on key moments", settings.hapticsEnabled, viewModel::setHapticsEnabled)
                }

                SectionLabel("STATISTICS", Modifier.padding(top = 8.dp))
                SettingsCard {
                    Row(Modifier.padding(vertical = 16.dp)) {
                        StatBlock(stats.gamesPlayed.toString(), "GAMES", Modifier.weight(1f))
                        StatBlock(stats.playerOneWins.toString(), "P1 WINS", Modifier.weight(1f), Player.PLAYER_ONE.accent)
                        StatBlock(stats.playerTwoWins.toString(), "P2 WINS", Modifier.weight(1f), Player.PLAYER_TWO.accent)
                        StatBlock(stats.draws.toString(), "DRAWS", Modifier.weight(1f))
                    }
                    CardDivider()
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Shortest Winning Path", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                stats.shortestWinningPath?.let { "$it pieces" } ?: "No wins yet",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        OutlinedButton(
                            onClick = { confirmReset = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                        ) {
                            Text("RESET", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset statistics?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Games played, wins, draws and the shortest path record will be cleared.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    viewModel.resetStatistics()
                }) { Text("RESET", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) {
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(20.dp),
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) { content() }
    }
}

@Composable
private fun CardDivider() = HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
private fun <T> ChoiceRow(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .selectableGroup(),
        ) {
            options.forEach { (value, label) ->
                val isSelected = value == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh)
                        .selectable(selected = isSelected, role = Role.RadioButton) { onSelect(value) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
