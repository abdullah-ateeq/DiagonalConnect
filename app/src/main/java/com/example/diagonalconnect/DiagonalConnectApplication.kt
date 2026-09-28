package com.example.diagonalconnect

import android.app.Application
import com.example.diagonalconnect.audio.SoundPlayer
import com.example.diagonalconnect.data.SettingsRepository

class DiagonalConnectApplication : Application() {
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val soundPlayer: SoundPlayer by lazy { SoundPlayer(this) }
}
