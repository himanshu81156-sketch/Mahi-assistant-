package com.example.live

import java.util.Locale
import kotlinx.coroutines.flow.StateFlow

enum class SessionState {
    DISCONNECTED,
    CONNECTING,
    LISTENING,
    SPEAKING,
}

enum class MahiLanguage(
    val displayName: String,
    val nativeLabel: String,
    val shortCode: String,
    val locale: Locale,
) {
    ENGLISH("English", "English", "EN", Locale.US),
    HINGLISH("Hinglish", "Hinglish", "HI-EN", Locale.Builder().setLanguage("en").setRegion("IN").build()),
    HINDI("Hindi", "हिन्दी", "HI", Locale.Builder().setLanguage("hi").setRegion("IN").build()),
    SPANISH("Spanish", "Español", "ES", Locale.Builder().setLanguage("es").setRegion("ES").build()),
    FRENCH("French", "Français", "FR", Locale.FRANCE),
    JAPANESE("Japanese", "日本語", "JA", Locale.JAPAN),
}

enum class VoiceCharacter(
    val displayName: String,
    val vibeTag: String,
    val pitch: Float,
    val speechRate: Float,
    val previewLine: String,
) {
    MAHI("Mahi", "Sassy Bestie", 1.10f, 1.02f, "Hey! Mahi back in her signature voice."),
    RIYA("Riya", "Warm & Bubbly", 1.22f, 1.04f, "Hi! Riya here, ready to brighten your day!"),
    KARAN("Karan", "Chill Desi Bro", 0.78f, 0.98f, "Kya haal hai bro? Karan speaking."),
    VIKRAM("Vikram", "Deep Alpha Boss", 0.65f, 0.90f, "Vikram here. Tell me the mission."),
    ZARA("Zara", "Playful & Bold", 1.16f, 0.96f, "Missed me? Zara is listening."),
    AARAV("Aarav", "Smooth Radio RJ", 0.86f, 1.08f, "Welcome back to the late night show with Aarav."),
    MEERA("Meera", "Soothing & Calm", 1.02f, 0.88f, "Take a deep breath. Meera is right here."),
    PIXEL("Pixel", "Anime Energy", 1.45f, 1.15f, "Yosh! Pixel power mode activated!"),
    NOVA("Nova", "Futuristic AI", 1.00f, 1.05f, "Nova neural core calibrated and ready."),
    TITAN("Titan", "Heavy Mecha", 0.55f, 0.82f, "Titan systems online. Awaiting command."),
    CHINTU("Chintu", "Chipmunk Kid", 1.65f, 1.22f, "Hehe! Chintu is super fast today!"),
    DADAJI("Dadaji", "Wise Storyteller", 0.72f, 0.80f, "Arre beta, suno ek acchi baat."),
    SIMRAN("Simran", "Expressive Drama", 1.18f, 1.06f, "Picture abhi baaki hai mere dost!"),
    ROCKY("Rocky", "Esports Gamer", 0.92f, 1.20f, "Let's go! Rocky is locked in!"),
    GHOST("Ghost", "Shadow Whisper", 0.62f, 0.78f, "Quiet... Ghost is in the system."),
    SUNNY("Sunny", "Cheerful Comedy", 1.28f, 1.18f, "Arre wah! Sunny mode full on hai!"),
    NEHA("Neha", "Crisp News Anchor", 1.05f, 1.00f, "Breaking update: Neha is live now."),
    KABIR("Kabir", "Deep Romantic", 0.76f, 0.86f, "Kabir here. Say something unforgettable."),
    ALIEN_X("Alien-X", "Cosmic Sci-Fi", 1.52f, 0.92f, "Greetings from orbit. Alien X connected."),
    JARVIS("Jarvis", "Tech Butler", 0.82f, 0.95f, "At your service, always."),
}

sealed interface MahiToolAction {
    data class OpenWebsite(
        val url: String,
        val label: String,
    ) : MahiToolAction
}

/**
 * Abstraction for Mahi's real-time voice-to-voice session.
 * All AI voice calls are routed through this interface.
 */
interface MahiLiveSession {
    val state: StateFlow<SessionState>
    val micLevel: StateFlow<Float>
    val speakerLevel: StateFlow<Float>
    val selectedLanguage: StateFlow<MahiLanguage>
    val selectedCharacter: StateFlow<VoiceCharacter>
    val lastToolAction: StateFlow<MahiToolAction?>
    val isDemoMode: Boolean

    fun connect(hasMicPermission: Boolean)
    fun disconnect()
    fun interrupt()
    fun setLanguage(language: MahiLanguage)
    fun setVoiceCharacter(character: VoiceCharacter)
    fun speakFeedback(text: String)
    fun triggerSampleTurn()
    fun triggerSampleWebsiteTool()
    fun clearToolAction()
    fun release()
}
