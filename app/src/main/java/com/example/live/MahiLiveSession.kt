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
    val lastToolAction: StateFlow<MahiToolAction?>
    val isDemoMode: Boolean

    fun connect(hasMicPermission: Boolean)
    fun disconnect()
    fun interrupt()
    fun setLanguage(language: MahiLanguage)
    fun triggerSampleTurn()
    fun triggerSampleWebsiteTool()
    fun clearToolAction()
    fun release()
}
