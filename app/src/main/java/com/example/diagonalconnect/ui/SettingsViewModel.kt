package com.example.diagonalconnect.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.diagonalconnect.DiagonalConnectApplication
import com.example.diagonalconnect.data.AppSettings
import com.example.diagonalconnect.data.BoardTheme
import com.example.diagonalconnect.data.GameStats
import com.example.diagonalconnect.data.SettingsRepository
import com.example.diagonalconnect.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> =
        repository.settings.stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val stats: StateFlow<GameStats> =
        repository.stats.stateIn(viewModelScope, SharingStarted.Eagerly, GameStats())

    fun setThemeMode(mode: ThemeMode) = launch { repository.setThemeMode(mode) }
    fun setBoardTheme(theme: BoardTheme) = launch { repository.setBoardTheme(theme) }
    fun setSoundEnabled(enabled: Boolean) = launch { repository.setSoundEnabled(enabled) }
    fun setHapticsEnabled(enabled: Boolean) = launch { repository.setHapticsEnabled(enabled) }
    fun setShowLastMove(enabled: Boolean) = launch { repository.setShowLastMove(enabled) }
    fun setHighlightEdges(enabled: Boolean) = launch { repository.setHighlightEdges(enabled) }
    fun resetStatistics() = launch { repository.resetStatistics() }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    companion object {
        fun factory(app: DiagonalConnectApplication): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(app.settingsRepository) }
        }
    }
}
