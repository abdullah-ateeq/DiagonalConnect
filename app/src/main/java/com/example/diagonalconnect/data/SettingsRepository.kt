package com.example.diagonalconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.diagonalconnect.game.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "diagonal_connect")

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class BoardTheme { CLASSIC, MIDNIGHT }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val boardTheme: BoardTheme = BoardTheme.CLASSIC,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showLastMove: Boolean = true,
    val highlightEdges: Boolean = true,
)

data class GameStats(
    val gamesPlayed: Int = 0,
    val playerOneWins: Int = 0,
    val playerTwoWins: Int = 0,
    val draws: Int = 0,
    val shortestWinningPath: Int? = null,
)

class SettingsRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    private val preferences: Flow<Preferences> = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    val settings: Flow<AppSettings> = preferences.map { prefs ->
        AppSettings(
            themeMode = enumOrDefault(prefs[Keys.THEME], ThemeMode.SYSTEM),
            boardTheme = enumOrDefault(prefs[Keys.BOARD_THEME], BoardTheme.CLASSIC),
            soundEnabled = prefs[Keys.SOUND] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS] ?: true,
            showLastMove = prefs[Keys.SHOW_LAST_MOVE] ?: true,
            highlightEdges = prefs[Keys.HIGHLIGHT_EDGES] ?: true,
        )
    }.distinctUntilChanged()

    val stats: Flow<GameStats> = preferences.map { prefs ->
        GameStats(
            gamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0,
            playerOneWins = prefs[Keys.P1_WINS] ?: 0,
            playerTwoWins = prefs[Keys.P2_WINS] ?: 0,
            draws = prefs[Keys.DRAWS] ?: 0,
            shortestWinningPath = prefs[Keys.SHORTEST_PATH],
        )
    }.distinctUntilChanged()

    suspend fun setThemeMode(mode: ThemeMode) = store.edit { it[Keys.THEME] = mode.name }

    suspend fun setBoardTheme(theme: BoardTheme) = store.edit { it[Keys.BOARD_THEME] = theme.name }

    suspend fun setSoundEnabled(enabled: Boolean) = store.edit { it[Keys.SOUND] = enabled }

    suspend fun setHapticsEnabled(enabled: Boolean) = store.edit { it[Keys.HAPTICS] = enabled }

    suspend fun setShowLastMove(enabled: Boolean) = store.edit { it[Keys.SHOW_LAST_MOVE] = enabled }

    suspend fun setHighlightEdges(enabled: Boolean) = store.edit { it[Keys.HIGHLIGHT_EDGES] = enabled }

    /** Records a finished match. [winner] is null for a draw. */
    suspend fun recordResult(winner: Player?, pathLength: Int) {
        store.edit { prefs ->
            prefs[Keys.GAMES_PLAYED] = (prefs[Keys.GAMES_PLAYED] ?: 0) + 1
            when (winner) {
                Player.PLAYER_ONE -> prefs[Keys.P1_WINS] = (prefs[Keys.P1_WINS] ?: 0) + 1
                Player.PLAYER_TWO -> prefs[Keys.P2_WINS] = (prefs[Keys.P2_WINS] ?: 0) + 1
                null -> prefs[Keys.DRAWS] = (prefs[Keys.DRAWS] ?: 0) + 1
            }
            if (winner != null && pathLength > 0) {
                val best = prefs[Keys.SHORTEST_PATH]
                if (best == null || pathLength < best) prefs[Keys.SHORTEST_PATH] = pathLength
            }
        }
    }

    suspend fun resetStatistics() {
        store.edit { prefs ->
            prefs.remove(Keys.GAMES_PLAYED)
            prefs.remove(Keys.P1_WINS)
            prefs.remove(Keys.P2_WINS)
            prefs.remove(Keys.DRAWS)
            prefs.remove(Keys.SHORTEST_PATH)
        }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, default: T): T =
        value?.let { name -> enumValues<T>().firstOrNull { it.name == name } } ?: default

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val HAPTICS = booleanPreferencesKey("haptics_enabled")
        val SHOW_LAST_MOVE = booleanPreferencesKey("show_last_move")
        val HIGHLIGHT_EDGES = booleanPreferencesKey("highlight_edges")
        val GAMES_PLAYED = intPreferencesKey("games_played")
        val P1_WINS = intPreferencesKey("player_one_wins")
        val P2_WINS = intPreferencesKey("player_two_wins")
        val DRAWS = intPreferencesKey("draws")
        val SHORTEST_PATH = intPreferencesKey("shortest_winning_path")
    }
}
