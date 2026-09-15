package com.kilo.videoeditor.presentation.home

import androidx.lifecycle.ViewModel
import com.kilo.videoeditor.domain.model.VideoProject
import com.kilo.videoeditor.domain.usecase.DeleteProjectUseCase
import com.kilo.videoeditor.domain.usecase.GetProjectsUseCase
import com.kilo.videoeditor.domain.usecase.ImportVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val projects: List<VideoProject> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val importInProgress: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProjects: GetProjectsUseCase,
    private val importVideo: ImportVideoUseCase,
    private val deleteProject: DeleteProjectUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getProjects().collect { projects ->
                    _uiState.update { it.copy(projects = projects, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun importVideo(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(importInProgress = true) }
            importVideo(uri).onSuccess { _ ->
                _uiState.update { it.copy(importInProgress = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(importInProgress = false, error = error.message) }
            }
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            deleteProject(id)
        }
    }
}