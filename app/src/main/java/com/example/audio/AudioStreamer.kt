package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.UUID
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "AudioStreamer"

/**
 * Handles real-time PCM16 16kHz microphone capture, 24kHz PCM16 audio playback,
 * amplitude metering for visualizers, and local TTS synthesis for Demo Mode.
 */
class AudioStreamer(context: Context) {

    companion object {
        const val INPUT_SAMPLE_RATE = 16000
        const val OUTPUT_SAMPLE_RATE = 24000
    }

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var audioRecord: AudioRecord? = null
    private var captureJob: Job? = null

    private var audioTrack: AudioTrack? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var speakerPulseJob: Job? = null
    private var ttsWatchdogJob: Job? = null

    private val _micAmplitude = MutableStateFlow(0f)
    val micAmplitude: StateFlow<Float> = _micAmplitude.asStateFlow()

    private val _speakerAmplitude = MutableStateFlow(0f)
    val speakerAmplitude: StateFlow<Float> = _speakerAmplitude.asStateFlow()

    private val _pcmInputChunks = MutableSharedFlow<ByteArray>(extraBufferCapacity = 32)
    val pcmInputChunks: SharedFlow<ByteArray> = _pcmInputChunks.asSharedFlow()

    private var onTtsComplete: (() -> Unit)? = null

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.12f)
                tts?.setSpeechRate(1.06f)
                ttsReady = true
            } else {
                Log.w(TAG, "TextToSpeech initialization failed with status: $status")
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                startSpeakerPulse()
            }

            override fun onDone(utteranceId: String?) {
                stopSpeakerPulse()
                onTtsComplete?.invoke()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                stopSpeakerPulse()
                onTtsComplete?.invoke()
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun startCapture(onError: (String) -> Unit = {}) {
        if (captureJob?.isActive == true) return

        val minBuffer = AudioRecord.getMinBufferSize(
            INPUT_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBuffer == AudioRecord.ERROR || minBuffer == AudioRecord.ERROR_BAD_VALUE) {
            onError("AudioRecord buffer initialization failed.")
            return
        }

        val bufferSize = maxOf(minBuffer * 2, 2048)
        try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                INPUT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
            )
            if (record.state != AudioRecord.STATE_INITIALIZED) {
                record.release()
                onError("Microphone hardware could not be initialized.")
                return
            }

            audioRecord = record
            record.startRecording()

            captureJob = scope.launch {
                val buffer = ShortArray(bufferSize / 2)
                val byteBuffer = ByteArray(buffer.size * 2)

                while (isActive) {
                    val readCount = record.read(buffer, 0, buffer.size)
                    if (readCount > 0) {
                        var sumSq = 0.0
                        for (i in 0 until readCount) {
                            val sample = buffer[i]
                            sumSq += sample * sample
                            byteBuffer[i * 2] = (sample.toInt() and 0xFF).toByte()
                            byteBuffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
                        }
                        val rms = sqrt(sumSq / readCount)
                        val normalized = (rms / 6500.0).toFloat().coerceIn(0f, 1f)
                        _micAmplitude.value = normalized
                        _pcmInputChunks.tryEmit(byteBuffer.copyOf(readCount * 2))
                    } else {
                        delay(16)
                    }
                }
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "RECORD_AUDIO permission missing", se)
            onError("Microphone permission required.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio capture", e)
            onError(e.localizedMessage ?: "Microphone capture failed.")
        }
    }

    fun stopCapture() {
        val jobToCancel = captureJob
        captureJob = null
        val recordToRelease = audioRecord
        audioRecord = null
        _micAmplitude.value = 0f

        scope.launch {
            jobToCancel?.cancel()
            try {
                recordToRelease?.stop()
            } catch (_: IllegalStateException) {
            }
            try {
                recordToRelease?.release()
            } catch (_: Exception) {
            }
        }
    }

    fun playPcm24kChunk(pcmData: ByteArray) {
        ensureAudioTrack()
        audioTrack?.write(pcmData, 0, pcmData.size)
    }

    fun setLanguage(locale: Locale) {
        if (!ttsReady) return
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "TTS locale $locale not supported, keeping current locale")
        }
    }

    fun speakDemoLine(text: String, onFinished: () -> Unit) {
        ttsWatchdogJob?.cancel()
        onTtsComplete = {
            ttsWatchdogJob?.cancel()
            onFinished()
        }
        if (ttsReady) {
            val utteranceId = UUID.randomUUID().toString()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            // Safety watchdog so UI never gets stuck in SPEAKING if TTS engine stalls
            ttsWatchdogJob = scope.launch {
                delay(9_000L)
                stopSpeakerPulse()
                onFinished()
            }
        } else {
            ttsWatchdogJob = scope.launch {
                startSpeakerPulse()
                delay(2200L)
                stopSpeakerPulse()
                onFinished()
            }
        }
    }

    fun interruptPlayback() {
        ttsWatchdogJob?.cancel()
        ttsWatchdogJob = null
        onTtsComplete = null
        tts?.stop()
        stopSpeakerPulse()
        val track = audioTrack
        if (track != null) {
            scope.launch {
                try {
                    track.pause()
                    track.flush()
                    track.play()
                } catch (_: IllegalStateException) {
                }
            }
        }
    }

    private fun ensureAudioTrack() {
        if (audioTrack != null) return
        val minBuf = AudioTrack.getMinBufferSize(
            OUTPUT_SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val track = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(OUTPUT_SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build(),
            maxOf(minBuf, 4096),
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        )
        track.play()
        audioTrack = track
    }

    private fun startSpeakerPulse() {
        speakerPulseJob?.cancel()
        speakerPulseJob = scope.launch {
            var phase = 0.0
            while (isActive) {
                phase += 0.42
                val wave = (0.45 + 0.45 * kotlin.math.sin(phase) * kotlin.math.cos(phase * 0.5))
                    .toFloat()
                    .coerceIn(0.15f, 1f)
                _speakerAmplitude.value = wave
                delay(40L)
            }
        }
    }

    private fun stopSpeakerPulse() {
        speakerPulseJob?.cancel()
        speakerPulseJob = null
        _speakerAmplitude.value = 0f
    }

    fun release() {
        stopCapture()
        interruptPlayback()
        audioTrack?.release()
        audioTrack = null
        tts?.shutdown()
        tts = null
        scope.cancel()
    }
}
