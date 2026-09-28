package com.example.diagonalconnect.audio

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

/** Minimal additive synthesiser producing 16-bit mono PCM WAV files. */
internal object ToneSynth {

    private const val SAMPLE_RATE = 44_100

    private class Partial(
        val frequency: Double,
        val start: Double,
        val duration: Double,
        val amplitude: Double,
        val decay: Double,
    )

    fun wav(sound: GameSound): ByteArray = encodeWav(render(partialsFor(sound)))

    private fun partialsFor(sound: GameSound): List<Partial> = when (sound) {
        GameSound.PLACE -> listOf(
            Partial(420.0, 0.0, 0.09, 0.8, 38.0),
            Partial(840.0, 0.0, 0.05, 0.25, 60.0),
            Partial(1260.0, 0.0, 0.03, 0.1, 80.0),
        )
        GameSound.TURN -> listOf(
            Partial(990.0, 0.0, 0.06, 0.5, 55.0),
        )
        GameSound.PATH_COMPLETE -> listOf(
            Partial(523.25, 0.0, 0.5, 0.5, 6.0),
            Partial(1046.5, 0.0, 0.3, 0.1, 10.0),
            Partial(783.99, 0.13, 0.6, 0.5, 5.0),
            Partial(1567.98, 0.13, 0.35, 0.08, 9.0),
            Partial(1046.5, 0.26, 0.7, 0.4, 4.5),
        )
        GameSound.DRAW -> listOf(
            Partial(392.0, 0.0, 0.45, 0.5, 7.0),
            Partial(329.63, 0.16, 0.5, 0.45, 6.0),
        )
    }

    private fun render(partials: List<Partial>): DoubleArray {
        val totalSeconds = partials.maxOf { it.start + it.duration }
        val samples = DoubleArray((totalSeconds * SAMPLE_RATE).toInt() + 1)
        for (p in partials) {
            val startIndex = (p.start * SAMPLE_RATE).toInt()
            val count = (p.duration * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val index = startIndex + i
                if (index >= samples.size) break
                val t = i.toDouble() / SAMPLE_RATE
                val attack = min(1.0, t / 0.004)
                val release = min(1.0, (p.duration - t) / 0.012)
                val envelope = attack * release * exp(-p.decay * t)
                samples[index] += p.amplitude * envelope * sin(2.0 * PI * p.frequency * t)
            }
        }
        val peak = samples.maxOf { abs(it) }
        if (peak > 0.9) {
            val scale = 0.9 / peak
            for (i in samples.indices) samples[i] *= scale
        }
        return samples
    }

    private fun encodeWav(samples: DoubleArray): ByteArray {
        val dataSize = samples.size * 2
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray(Charsets.US_ASCII))
            putInt(36 + dataSize)
            put("WAVE".toByteArray(Charsets.US_ASCII))
            put("fmt ".toByteArray(Charsets.US_ASCII))
            putInt(16)
            putShort(1)
            putShort(1)
            putInt(SAMPLE_RATE)
            putInt(SAMPLE_RATE * 2)
            putShort(2)
            putShort(16)
            put("data".toByteArray(Charsets.US_ASCII))
            putInt(dataSize)
        }
        val pcm = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
        for (s in samples) pcm.putShort((s.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort())
        return ByteArrayOutputStream(44 + dataSize).apply {
            write(header.array())
            write(pcm.array())
        }.toByteArray()
    }
}
