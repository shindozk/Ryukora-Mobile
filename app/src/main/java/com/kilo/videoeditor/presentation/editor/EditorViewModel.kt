package com.kilo.videoeditor.presentation.editor

import androidx.lifecycle.ViewModel
import com.kilo.videoeditor.domain.model.EditorData
import com.kilo.videoeditor.domain.model.EditorUiState
import com.kilo.videoeditor.domain.model.ExportState
import com.kilo.videoeditor.domain.usecase.ExportProjectUseCase
import com.kilo.videoeditor.domain.usecase.GetEditorDataUseCase
import com.kilo.videoeditor.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val getEditorData: GetEditorDataUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val exportProject: ExportProjectUseCase,
) : ViewModel() {

    private val projectId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProject(id: Long) {
        projectId.value = id
        viewModelScope.launch {
            getEditorData(id).collect { data ->
                _uiState.update {
                    it.copy(
                        project = data.project,
                        clips = data.clips,
                        overlays = data.overlays,
                        effects = data.effects,
                    )
                }
            }
        }
    }

    fun updateSettings(settings: com.kilo.videoeditor.domain.model.EditSettings) {
        val id = projectId.value ?: return
        _uiState.update { it.copy(settings = settings) }
        viewModelScope.launch { updateSettings(id, settings) }
    }

    fun setPlaying(playing: Boolean) {
        _uiState.update { it.copy(isPlaying = playing) }
    }

    fun setCurrentTime(ms: Long) {
        _uiState.update { it.copy(currentTimeMs = ms) }
    }

    fun export() {
        val id = projectId.value ?: return
        val settings = _uiState.value.settings
        _uiState.update { it.copy(isExporting = true, exportState = ExportState.Idle) }
        viewModelScope.launch {
            exportProject(id, settings).collect { state ->
                _uiState.update {
                    it.copy(
                        exportState = state,
                        isExporting = state !is ExportState.Success && state !is ExportState.Error,
                    )
                }
            }
        }
    }

    fun cancelExport() {
        _uiState.update { it.copy(isExporting = false, exportState = ExportState.Idle) }
    }
}