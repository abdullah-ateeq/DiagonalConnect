package com.example.diagonalconnect

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diagonalconnect.ui.DiagonalConnectApp
import com.example.diagonalconnect.ui.GameViewModel
import com.example.diagonalconnect.ui.SettingsViewModel
import com.example.diagonalconnect.ui.theme.DiagonalConnectTheme
import com.example.diagonalconnect.ui.theme.isDark

class MainActivity : ComponentActivity() {

    private val app: DiagonalConnectApplication
        get() = application as DiagonalConnectApplication

    private val gameViewModel: GameViewModel by viewModels { GameViewModel.factory(app) }
    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.factory(app) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val dark = settings.themeMode.isDark()
            DisposableEffect(dark) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }
            DiagonalConnectTheme(themeMode = settings.themeMode, boardTheme = settings.boardTheme) {
                DiagonalConnectApp(gameViewModel, settingsViewModel)
            }
        }
    }
}
