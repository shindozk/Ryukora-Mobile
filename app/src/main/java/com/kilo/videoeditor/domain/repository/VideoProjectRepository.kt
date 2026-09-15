package com.kilo.videoeditor.domain.repository

import com.kilo.videoeditor.domain.model.AudioEffect
import com.kilo.videoeditor.domain.model.Clip
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.TextOverlay
import com.kilo.videoeditor.domain.model.VideoProject
import kotlinx.coroutines.flow.Flow

/**
 * Repositório de projetos de vídeo.
 */
interface VideoProjectRepository {
    fun getAllProjects(): Flow<List<VideoProject>>
    suspend fun getProject(id: Long): VideoProject?
    suspend fun insertProject(project: VideoProject): Long
    suspend fun updateProject(project: VideoProject)
    suspend fun deleteProject(id: Long)
    suspend fun updateSettings(id: Long, settings: EditSettings)
}

/**
 * Repositório de clipes.
 */
interface ClipRepository {
    suspend fun getClipsForProject(projectId: Long): List<Clip>
    suspend fun insertClip(clip: Clip): Long
    suspend fun deleteClip(clip: Clip)
    suspend fun reorderClips(clips: List<Clip>)
}

/**
 * Repositório de overlays de texto.
 */
interface TextOverlayRepository {
    suspend fun getOverlaysForProject(projectId: Long): List<TextOverlay>
    suspend fun insertOverlay(overlay: TextOverlay): Long
    suspend fun updateOverlay(overlay: TextOverlay)
    suspend fun deleteOverlay(overlay: TextOverlay)
}

/**
 * Repositório de efeitos de áudio.
 */
interface AudioEffectRepository {
    suspend fun getEffectsForProject(projectId: Long): List<AudioEffect>
    suspend fun insertEffect(effect: AudioEffect): Long
    suspend fun deleteEffect(effect: AudioEffect)
}

/**
 * Repositório de exportação.
 */
interface ExportRepository {
    suspend fun exportProject(
        projectId: Long,
        settings: EditSettings,
        onProgress: (Int) -> Unit,
    ): Result<String>
    suspend fun cancelExport()
}

/**
 * Repositório de mídia (importação de vídeos do dispositivo).
 */
interface MediaRepository {
    suspend fun getVideos(): List<VideoProject>
    suspend fun importVideo(uri: String): Result<VideoProject>
}