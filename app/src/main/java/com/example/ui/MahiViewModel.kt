package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioStreamer
import com.example.chat.ChatMessage
import com.example.chat.ChatRepository
import com.example.live.DemoMahiLiveSession
import com.example.live.MahiLanguage
import com.example.live.MahiLiveSession
import com.example.live.MahiToolAction
import com.example.live.SessionState
import com.example.live.VoiceCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MahiUiState(
    val sessionState: SessionState = SessionState.DISCONNECTED,
    val micLevel: Float = 0f,
    val speakerLevel: Float = 0f,
    val selectedLanguage: MahiLanguage = MahiLanguage.ENGLISH,
    val selectedCharacter: VoiceCharacter = VoiceCharacter.MAHI,
    val hasMicPermission: Boolean = false,
    val isDemoMode: Boolean = true,
    val pendingToolAction: MahiToolAction? = null,
)

class MahiViewModel(application: Application) : AndroidViewModel(application) {

    private val audioStreamer = AudioStreamer(application)
    private val liveSession: MahiLiveSession = DemoMahiLiveSession(audioStreamer)
    private val chatRepository = ChatRepository(application)

    private val _hasMicPermission = MutableStateFlow(false)
    private val _chatMessages = MutableStateFlow(chatRepository.loadHistory())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatReplying = MutableStateFlow(false)
    val isChatReplying: StateFlow<Boolean> = _isChatReplying.asStateFlow()

    val uiState: StateFlow<MahiUiState> = combine(
        liveSession.state,
        liveSession.micLevel,
        liveSession.speakerLevel,
        liveSession.selectedLanguage,
        liveSession.selectedCharacter,
        _hasMicPermission,
        liveSession.lastToolAction,
    ) { values ->
        val sessionState = values[0] as SessionState
        val micLevel = values[1] as Float
        val speakerLevel = values[2] as Float
        val selectedLanguage = values[3] as MahiLanguage
        val selectedCharacter = values[4] as VoiceCharacter
        val hasMicPermission = values[5] as Boolean
        val toolAction = values[6] as MahiToolAction?
        MahiUiState(
            sessionState = sessionState,
            micLevel = micLevel,
            speakerLevel = speakerLevel,
            selectedLanguage = selectedLanguage,
            selectedCharacter = selectedCharacter,
            hasMicPermission = hasMicPermission,
            isDemoMode = liveSession.isDemoMode,
            pendingToolAction = toolAction,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MahiUiState(isDemoMode = liveSession.isDemoMode),
    )

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _isChatReplying.value) return
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            text = trimmed,
            isUser = true,
        )
        val withUser = _chatMessages.value + userMsg
        _chatMessages.value = withUser
        chatRepository.saveHistory(withUser)

        viewModelScope.launch {
            _isChatReplying.value = true
            val replyText = chatRepository.generateReply(trimmed)
            val botMsg = ChatMessage(
                id = "a_${System.currentTimeMillis()}",
                text = replyText,
                isUser = false,
            )
            val updated = _chatMessages.value + botMsg
            _chatMessages.value = updated
            chatRepository.saveHistory(updated)
            _isChatReplying.value = false
            liveSession.speakFeedback(replyText)
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = chatRepository.clearHistory()
    }

    fun updateMicPermission(granted: Boolean) {
        _hasMicPermission.value = granted
    }

    fun onSelectLanguage(language: MahiLanguage) {
        liveSession.setLanguage(language)
    }

    fun onSelectVoiceCharacter(character: VoiceCharacter) {
        liveSession.setVoiceCharacter(character)
    }

    fun speakAssistantFeedback(text: String) {
        liveSession.speakFeedback(text)
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
