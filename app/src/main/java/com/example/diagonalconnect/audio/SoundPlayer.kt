package com.example.diagonalconnect.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.util.concurrent.ConcurrentHashMap

enum class GameSound(val volume: Float) {
    PLACE(0.55f),
    TURN(0.18f),
    PATH_COMPLETE(0.6f),
    DRAW(0.45f),
}

/**
 * Plays short, restrained effects. The waveforms are synthesised once into the cache
 * directory, so the app ships without audio assets.
 */
class SoundPlayer(context: Context) {

    private val appContext = context.applicationContext
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()
    private val soundIds = ConcurrentHashMap<GameSound, Int>()
    private val loadedIds: MutableSet<Int> = ConcurrentHashMap.newKeySet()

    @Volatile
    private var prepared = false

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedIds.add(sampleId)
        }
    }

    /** Writes and loads the effects. Blocking; call from a background dispatcher. */
    fun prepare() {
        if (prepared) return
        synchronized(this) {
            if (prepared) return
            GameSound.entries.forEach { sound ->
                val file = File(appContext.cacheDir, "sfx_v1_${sound.name.lowercase()}.wav")
                if (!file.exists() || file.length() == 0L) file.writeBytes(ToneSynth.wav(sound))
                soundIds[sound] = soundPool.load(file.absolutePath, 1)
            }
            prepared = true
        }
    }

    fun play(sound: GameSound) {
        val id = soundIds[sound] ?: return
        if (id !in loadedIds) return
        soundPool.play(id, sound.volume, sound.volume, 1, 0, 1f)
    }
}
