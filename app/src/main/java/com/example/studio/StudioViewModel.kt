package com.example.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudioUiState(
    val appPrompt: String = "",
    val isBuildingApp: Boolean = false,
    val appBlueprint: AppBlueprint? = null,
    val mediaPrompt: String = "",
    val selectedMediaType: MediaType = MediaType.PHOTO,
    val selectedAspectRatio: AspectRatioOption = AspectRatioOption.SQUARE,
    val referencePhotoUri: String? = null,
    val isGeneratingMedia: Boolean = false,
    val generatedMedia: GeneratedMediaResult? = null,
    val isVideoPlaying: Boolean = true,
    val isDemoMode: Boolean = true,
)

class StudioViewModel(
    private val repository: StudioRepository = DemoStudioRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState(isDemoMode = repository.isDemoMode))
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    fun onAppPromptChange(value: String) {
        _uiState.update { it.copy(appPrompt = value) }
    }

    fun buildApp() {
        val prompt = _uiState.value.appPrompt
        if (_uiState.value.isBuildingApp) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBuildingApp = true) }
            val blueprint = repository.generateAppBlueprint(prompt)
            _uiState.update { it.copy(isBuildingApp = false, appBlueprint = blueprint) }
        }
    }

    fun onMediaPromptChange(value: String) {
        _uiState.update { it.copy(mediaPrompt = value) }
    }

    fun onSelectMediaType(type: MediaType) {
        _uiState.update { it.copy(selectedMediaType = type) }
    }

    fun onSelectAspectRatio(ratio: AspectRatioOption) {
        _uiState.update { it.copy(selectedAspectRatio = ratio) }
    }

    fun toggleVideoPlayback() {
        _uiState.update { it.copy(isVideoPlaying = !it.isVideoPlaying) }
    }

    fun onReferencePhotoSelected(uri: String?) {
        _uiState.update { it.copy(referencePhotoUri = uri) }
    }

    fun generateMedia() {
        val current = _uiState.value
        if (current.isGeneratingMedia) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingMedia = true) }
            val result = repository.generateMedia(
                prompt = current.mediaPrompt,
                type = current.selectedMediaType,
                aspectRatio = current.selectedAspectRatio,
                referencePhotoUri = current.referencePhotoUri,
            )
            _uiState.update {
                it.copy(
                    isGeneratingMedia = false,
                    generatedMedia = result,
                    isVideoPlaying = true,
                )
            }
        }
    }
}
