package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioStreamer
import com.example.live.DemoMahiLiveSession
import com.example.live.MahiLanguage
import com.example.live.MahiLiveSession
import com.example.live.MahiToolAction
import com.example.live.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MahiUiState(
    val sessionState: SessionState = SessionState.DISCONNECTED,
    val micLevel: Float = 0f,
    val speakerLevel: Float = 0f,
    val selectedLanguage: MahiLanguage = MahiLanguage.ENGLISH,
    val hasMicPermission: Boolean = false,
    val isDemoMode: Boolean = true,
    val pendingToolAction: MahiToolAction? = null,
)

class MahiViewModel(application: Application) : AndroidViewModel(application) {

    private val audioStreamer = AudioStreamer(application)
    private val liveSession: MahiLiveSession = DemoMahiLiveSession(audioStreamer)

    private val _hasMicPermission = MutableStateFlow(false)

    val uiState: StateFlow<MahiUiState> = combine(
        liveSession.state,
        liveSession.micLevel,
        liveSession.speakerLevel,
        liveSession.selectedLanguage,
        _hasMicPermission,
        liveSession.lastToolAction,
    ) { values ->
        val sessionState = values[0] as SessionState
        val micLevel = values[1] as Float
        val speakerLevel = values[2] as Float
        val selectedLanguage = values[3] as MahiLanguage
        val hasMicPermission = values[4] as Boolean
        val toolAction = values[5] as MahiToolAction?
        MahiUiState(
            sessionState = sessionState,
            micLevel = micLevel,
            speakerLevel = speakerLevel,
            selectedLanguage = selectedLanguage,
            hasMicPermission = hasMicPermission,
            isDemoMode = liveSession.isDemoMode,
            pendingToolAction = toolAction,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MahiUiState(isDemoMode = liveSession.isDemoMode),
    )

    fun updateMicPermission(granted: Boolean) {
        _hasMicPermission.value = granted
    }

    fun onSelectLanguage(language: MahiLanguage) {
        liveSession.setLanguage(language)
    }

    fun onCentralButtonTap() {
        when (liveSession.state.value) {
            SessionState.DISCONNECTED -> liveSession.connect(_hasMicPermission.value)
            SessionState.CONNECTING -> liveSession.disconnect()
            SessionState.LISTENING -> liveSession.triggerSampleTurn()
            SessionState.SPEAKING -> liveSession.interrupt()
        }
    }

    fun onEndSession() {
        liveSession.disconnect()
    }

    fun onTriggerSampleTurn() {
        liveSession.triggerSampleTurn()
    }

    fun onTriggerWebsiteTool() {
        liveSession.triggerSampleWebsiteTool()
    }

    fun onToolActionConsumed() {
        liveSession.clearToolAction()
    }

    override fun onCleared() {
        super.onCleared()
        liveSession.release()
        audioStreamer.release()
    }
}
