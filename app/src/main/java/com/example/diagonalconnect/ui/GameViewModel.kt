package com.example.diagonalconnect.ui

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.diagonalconnect.DiagonalConnectApplication
import com.example.diagonalconnect.audio.GameSound
import com.example.diagonalconnect.audio.SoundPlayer
import com.example.diagonalconnect.data.AppSettings
import com.example.diagonalconnect.data.SettingsRepository
import com.example.diagonalconnect.game.DiagonalConnectGame
import com.example.diagonalconnect.game.DiagonalConnectState
import com.example.diagonalconnect.game.GridPosition
import com.example.diagonalconnect.game.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PLACEMENT_ANIMATION_MS = 160L
private const val PATH_COMPLETE_DISPLAY_MS = 1_700L
private const val DRAW_DISPLAY_MS = 1_200L

enum class MatchBanner { PATH_COMPLETE, DRAW }

enum class GameFeedback { PLACED, PATH_COMPLETE, DRAW }

data class GameUiState(
    val game: DiagonalConnectState = DiagonalConnectState(),
    val canUndo: Boolean = false,
    val banner: MatchBanner? = null,
    val resultReady: Boolean = false,
)

data class MatchSummary(
    val finalState: DiagonalConnectState,
    val winner: Player?,
    val moves: Int,
    val pathLength: Int,
    val playerOnePieces: Int,
    val playerTwoPieces: Int,
    val durationMillis: Long,
)

class GameViewModel(
    private val repository: SettingsRepository,
    private val soundPlayer: SoundPlayer,
) : ViewModel() {

    private val game = DiagonalConnectGame()
    private var matchStartedAt = SystemClock.elapsedRealtime()
    private var turnJob: Job? = null

    private val settings: StateFlow<AppSettings> =
        repository.settings.stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _summary = MutableStateFlow<MatchSummary?>(null)
    val summary: StateFlow<MatchSummary?> = _summary.asStateFlow()

    private val _feedback = MutableSharedFlow<GameFeedback>(extraBufferCapacity = 4)
    val feedback: SharedFlow<GameFeedback> = _feedback.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) { soundPlayer.prepare() }
    }

    fun onCellTapped(position: GridPosition) {
        if (!game.placePiece(position)) return
        val placed = game.state
        publish()
        _feedback.tryEmit(GameFeedback.PLACED)
        play(GameSound.PLACE)

        turnJob = viewModelScope.launch {
            delay(PLACEMENT_ANIMATION_MS)
            game.finishAnimation()
            when {
                placed.winner != null -> {
                    concludeMatch()
                    publish(banner = MatchBanner.PATH_COMPLETE)
                    _feedback.tryEmit(GameFeedback.PATH_COMPLETE)
                    play(GameSound.PATH_COMPLETE)
                    delay(PATH_COMPLETE_DISPLAY_MS)
                    _uiState.update { it.copy(resultReady = true) }
                }
                placed.isDraw -> {
                    concludeMatch()
                    publish(banner = MatchBanner.DRAW)
                    _feedback.tryEmit(GameFeedback.DRAW)
                    play(GameSound.DRAW)
                    delay(DRAW_DISPLAY_MS)
                    _uiState.update { it.copy(resultReady = true) }
                }
                else -> {
                    publish()
                    play(GameSound.TURN)
                }
            }
        }
    }

    fun undo() {
        if (game.state.isGameOver) return
        if (game.undo()) publish()
    }

    fun newMatch() {
        turnJob?.cancel()
        turnJob = null
        game.restart()
        matchStartedAt = SystemClock.elapsedRealtime()
        _uiState.value = GameUiState(game = game.state)
    }

    fun onResultShown() {
        _uiState.update { it.copy(resultReady = false) }
    }

    private fun publish(banner: MatchBanner? = _uiState.value.banner) {
        val state = game.state
        _uiState.update {
            it.copy(
                game = state,
                canUndo = game.canUndo && !state.isGameOver,
                banner = banner,
            )
        }
    }

    private fun concludeMatch() {
        val state = game.state
        val summary = MatchSummary(
            finalState = state,
            winner = state.winner,
            moves = state.moveCount,
            pathLength = state.winningPath.size,
            playerOnePieces = state.pieceCount(Player.PLAYER_ONE),
            playerTwoPieces = state.pieceCount(Player.PLAYER_TWO),
            durationMillis = SystemClock.elapsedRealtime() - matchStartedAt,
        )
        _summary.value = summary
        viewModelScope.launch { repository.recordResult(summary.winner, summary.pathLength) }
    }

    private fun play(sound: GameSound) {
        if (settings.value.soundEnabled) soundPlayer.play(sound)
    }

    companion object {
        fun factory(app: DiagonalConnectApplication): ViewModelProvider.Factory = viewModelFactory {
            initializer { GameViewModel(app.settingsRepository, app.soundPlayer) }
        }
    }
}
