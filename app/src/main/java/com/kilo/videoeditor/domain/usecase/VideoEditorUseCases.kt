package com.kilo.videoeditor.domain.usecase

import com.kilo.videoeditor.domain.model.AudioEffect
import com.kilo.videoeditor.domain.model.Clip
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.TextOverlay
import com.kilo.videoeditor.domain.model.VideoProject
import com.kilo.videoeditor.domain.model.ExportState
import com.kilo.videoeditor.domain.repository.AudioEffectRepository
import com.kilo.videoeditor.domain.repository.ClipRepository
import com.kilo.videoeditor.domain.repository.MediaRepository
import com.kilo.videoeditor.domain.repository.TextOverlayRepository
import com.kilo.videoeditor.domain.repository.VideoProjectRepository
import com.kilo.videoeditor.domain.repository.ExportRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProjectsUseCase @Inject constructor(
    private val repository: VideoProjectRepository,
) {
    operator fun invoke(): Flow<List<VideoProject>> = repository.getAllProjects()
}

@Singleton
class ImportVideoUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val projectRepository: VideoProjectRepository,
) {
    suspend operator fun invoke(uri: String): Result<Long> {
        return mediaRepository.importVideo(uri).mapCatching { project ->
            projectRepository.insertProject(project)
        }
    }
}

@Singleton
class DeleteProjectUseCase @Inject constructor(
    private val repository: VideoProjectRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteProject(id)
}

data class EditorData(
    val project: VideoProject?,
    val clips: List<Clip>,
    val overlays: List<TextOverlay>,
    val effects: List<AudioEffect>,
)

@Singleton
class GetEditorDataUseCase @Inject constructor(
    private val projectRepository: VideoProjectRepository,
    private val clipRepository: ClipRepository,
    private val textOverlayRepository: TextOverlayRepository,
    private val audioEffectRepository: AudioEffectRepository,
) {
    operator fun invoke(projectId: Long): Flow<EditorData> = flow {
        val project = projectRepository.getProject(projectId)
        val clips = clipRepository.getClipsForProject(projectId)
        val overlays = textOverlayRepository.getOverlaysForProject(projectId)
        val effects = audioEffectRepository.getEffectsForProject(projectId)
        emit(EditorData(project, clips, overlays, effects))
    }
}

@Singleton
class UpdateSettingsUseCase @Inject constructor(
    private val repository: VideoProjectRepository,
) {
    suspend operator fun invoke(projectId: Long, settings: EditSettings) =
        repository.updateSettings(projectId, settings)
}

@Singleton
class ExportProjectUseCase @Inject constructor(
    private val exportRepository: ExportRepository,
) {
    operator fun invoke(
        projectId: Long,
        settings: EditSettings,
    ): Flow<ExportState> = channelFlow {
        exportRepository.exportProject(projectId, settings) { percent ->
            send(ExportState.Progress(percent))
        }.onSuccess { path ->
            send(ExportState.Success(path))
        }.onFailure { error ->
            send(ExportState.Error(error.message ?: "Erro na exportação"))
        }
    }
}