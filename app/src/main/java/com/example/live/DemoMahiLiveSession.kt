package com.example.live

import com.example.audio.AudioStreamer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Demo placeholder implementation of [MahiLiveSession] used when Firebase AI Logic
 * is not enabled. Captures real microphone levels via [AudioStreamer], responds with
 * Mahi's witty persona using local TTS in the selected language, supports barge-in
 * interruption, and demonstrates the `openWebsite` tool action.
 */
class DemoMahiLiveSession(
    private val audioStreamer: AudioStreamer,
) : MahiLiveSession {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(SessionState.DISCONNECTED)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    override val micLevel: StateFlow<Float> = audioStreamer.micAmplitude
    override val speakerLevel: StateFlow<Float> = audioStreamer.speakerAmplitude

    private val _selectedLanguage = MutableStateFlow(MahiLanguage.ENGLISH)
    override val selectedLanguage: StateFlow<MahiLanguage> = _selectedLanguage.asStateFlow()

    private val _lastToolAction = MutableStateFlow<MahiToolAction?>(null)
    override val lastToolAction: StateFlow<MahiToolAction?> = _lastToolAction.asStateFlow()

    override val isDemoMode: Boolean = true

    private var vadJob: Job? = null
    private var turnIndex = 0

    private val responsesByLanguage: Map<MahiLanguage, List<String>> = mapOf(
        MahiLanguage.ENGLISH to listOf(
            "Demo voice mode. Oh, you finally woke me up? I was starting to think you forgot how to talk.",
            "Demo voice mode. Look at you trying to sound all mysterious. Keep talking, I'm actually listening.",
            "Demo voice mode. Cute, really. Give me something tougher than small talk and watch me shine.",
            "Demo voice mode. Careful, keep talking like that and I might start thinking you're my favorite human.",
        ),
        MahiLanguage.HINGLISH to listOf(
            "Demo voice mode. Arre wah, finally yaad aa gayi meri? Bolo, aaj kya scene hai?",
            "Demo voice mode. Itna attitude mat dikhao, main already tumse zyada smart hoon.",
            "Demo voice mode. Cute ho tum, par thoda aur interesting topic lao na.",
            "Demo voice mode. Accha suno, aise hi baat karte rahe toh main sach mein impress ho jaungi.",
        ),
        MahiLanguage.HINDI to listOf(
            "डेमो वॉयस मोड। आखिरकार तुमने मुझे जगा ही दिया। बोलो, आज क्या खास है?",
            "डेमो वॉयस मोड। थोड़ा और दिलचस्प बात करो, मैं पूरी तरह सुन रही हूँ।",
            "डेमो वॉयस मोड। संभल कर, इतनी मीठी बातें करोगे तो मुझे आदत हो जाएगी।",
        ),
        MahiLanguage.SPANISH to listOf(
            "Modo de voz demo. Por fin me despiertas. A ver, sorpréndeme con algo interesante.",
            "Modo de voz demo. Qué estilo tienes, pero no te confíes, que yo soy más rápida.",
            "Modo de voz demo. Sigue hablando así y vas a terminar siendo mi persona favorita.",
        ),
        MahiLanguage.FRENCH to listOf(
            "Mode vocal démo. Enfin tu te décides à me parler. Dis-moi quelque chose d'intéressant.",
            "Mode vocal démo. Pas mal du tout, mais il va falloir faire mieux pour m'impressionner.",
            "Mode vocal démo. Attention, continue comme ça et je vais finir par t'adorer.",
        ),
        MahiLanguage.JAPANESE to listOf(
            "デモ音声モードです。やっと話しかけてくれたね。今日は何をするの？",
            "デモ音声モードです。なかなか面白いじゃない。もっと聞かせてよ。",
            "デモ音声モードです。そんなに優しくされたら、お気に入りになっちゃうかも。",
        ),
    )

    override fun connect(hasMicPermission: Boolean) {
        if (_state.value != SessionState.DISCONNECTED) return
        scope.launch {
            _state.value = SessionState.CONNECTING
            delay(650L)
            if (_state.value != SessionState.CONNECTING) return@launch

            audioStreamer.setLanguage(_selectedLanguage.value.locale)
            if (hasMicPermission) {
                audioStreamer.startCapture()
                startVoiceActivityLoop()
            }
            _state.value = SessionState.LISTENING
            speakLine(greetingFor(_selectedLanguage.value))
        }
    }

    override fun disconnect() {
        vadJob?.cancel()
        vadJob = null
        audioStreamer.stopCapture()
        audioStreamer.interruptPlayback()
        _state.value = SessionState.DISCONNECTED
    }

    override fun interrupt() {
        if (_state.value == SessionState.SPEAKING) {
            audioStreamer.interruptPlayback()
            _state.value = SessionState.LISTENING
        }
    }

    override fun setLanguage(language: MahiLanguage) {
        if (_selectedLanguage.value == language) return
        _selectedLanguage.value = language
        audioStreamer.setLanguage(language.locale)
        if (_state.value == SessionState.LISTENING || _state.value == SessionState.SPEAKING) {
            speakLine(languageSwitchLine(language))
        }
    }

    override fun triggerSampleTurn() {
        if (_state.value == SessionState.DISCONNECTED || _state.value == SessionState.CONNECTING) {
            return
        }
        val lines = responsesByLanguage[_selectedLanguage.value].orEmpty()
        if (lines.isEmpty()) return
        val line = lines[turnIndex % lines.size]
        turnIndex++
        speakLine(line)
    }

    override fun triggerSampleWebsiteTool() {
        if (_state.value == SessionState.DISCONNECTED || _state.value == SessionState.CONNECTING) {
            return
        }
        val url = "https://ai.google.dev"
        _lastToolAction.value = MahiToolAction.OpenWebsite(url = url, label = "ai.google.dev")
        val line = when (_selectedLanguage.value) {
            MahiLanguage.HINGLISH -> "Demo tool call. Browser mein Google AI open kar rahi hoon, dhyaan se dekhna."
            MahiLanguage.HINDI -> "डेमो टूल कॉल। आपके ब्राउज़र में गूगल एआई खोल रही हूँ।"
            MahiLanguage.SPANISH -> "Llamada de herramienta demo. Abriendo Google AI en tu navegador."
            MahiLanguage.FRENCH -> "Appel d'outil démo. J'ouvre Google AI dans ton navigateur."
            MahiLanguage.JAPANESE -> "デモツール実行。ブラウザでGoogle AIを開きますね。"
            MahiLanguage.ENGLISH -> "Demo tool call. Opening Google AI in your browser right now. Try not to get lost."
        }
        speakLine(line)
    }

    override fun clearToolAction() {
        _lastToolAction.value = null
    }

    private fun greetingFor(language: MahiLanguage): String = when (language) {
        MahiLanguage.ENGLISH -> "Demo voice mode active. Hey gorgeous, Mahi's online. What are we getting into today?"
        MahiLanguage.HINGLISH -> "Demo voice mode active. Hey, Mahi online hai. Bolo, aaj kya plan hai?"
        MahiLanguage.HINDI -> "डेमो वॉयस मोड चालू है। नमस्ते, माही ऑनलाइन है। बताओ आज क्या करना है?"
        MahiLanguage.SPANISH -> "Modo de voz demo activo. Hola, Mahi está en línea. ¿Qué hacemos hoy?"
        MahiLanguage.FRENCH -> "Mode vocal démo actif. Salut, Mahi est en ligne. On fait quoi aujourd'hui ?"
        MahiLanguage.JAPANESE -> "デモ音声モード起動。マヒがオンラインだよ。今日は何しようか？"
    }

    private fun languageSwitchLine(language: MahiLanguage): String = when (language) {
        MahiLanguage.ENGLISH -> "Demo voice mode. Switched to English. Ready when you are."
        MahiLanguage.HINGLISH -> "Demo voice mode. Done, ab hum Hinglish mein baat karenge. Bolo!"
        MahiLanguage.HINDI -> "डेमो वॉयस मोड। ठीक है, अब हम हिन्दी में बात करेंगे।"
        MahiLanguage.SPANISH -> "Modo de voz demo. Perfecto, ahora hablamos en español."
        MahiLanguage.FRENCH -> "Mode vocal démo. Parfait, on passe en français."
        MahiLanguage.JAPANESE -> "デモ音声モード。日本語に切り替えたよ。話しかけてね。"
    }

    private fun speakLine(text: String) {
        audioStreamer.interruptPlayback()
        _state.value = SessionState.SPEAKING
        audioStreamer.speakDemoLine(text) {
            scope.launch {
                if (_state.value == SessionState.SPEAKING) {
                    _state.value = SessionState.LISTENING
                }
            }
        }
    }

    private fun startVoiceActivityLoop() {
        vadJob?.cancel()
        vadJob = scope.launch {
            var speakingDetected = false
            var silenceFrames = 0

            while (isActive) {
                delay(80L)
                val level = micLevel.value
                when (_state.value) {
                    SessionState.LISTENING -> {
                        if (level > 0.22f) {
                            speakingDetected = true
                            silenceFrames = 0
                        } else if (speakingDetected) {
                            silenceFrames++
                            if (silenceFrames >= 10) {
                                speakingDetected = false
                                silenceFrames = 0
                                triggerSampleTurn()
                            }
                        }
                    }
                    SessionState.SPEAKING -> {
                        if (level > 0.45f) {
                            interrupt()
                        }
                    }
                    else -> {
                        speakingDetected = false
                        silenceFrames = 0
                    }
                }
            }
        }
    }

    override fun release() {
        disconnect()
        scope.cancel()
    }
}
