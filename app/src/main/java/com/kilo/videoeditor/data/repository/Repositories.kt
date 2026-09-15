package com.kilo.videoeditor.data.repository

import android.util.Log
import com.kilo.videoeditor.data.local.DatabaseProvider
import com.kilo.videoeditor.data.local.mapper.toDomain
import com.kilo.videoeditor.data.local.mapper.toEntity
import com.kilo.videoeditor.data.local.videoProjectDao
import com.kilo.videoeditor.data.local.editSettingsDao
import com.kilo.videoeditor.data.local.clipDao
import com.kilo.videoeditor.data.local.textOverlayDao
import com.kilo.videoeditor.data.local.audioEffectDao
import com.kilo.videoeditor.domain.model.AudioEffect
import com.kilo.videoeditor.domain.model.Clip
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.TextOverlay
import com.kilo.videoeditor.domain.model.VideoProject
import com.kilo.videoeditor.domain.model.ExportState
import com.kilo.videoeditor.domain.repository.AudioEffectRepository
import com.kilo.videoeditor.domain.repository.ClipRepository
import com.kilo.videoeditor.domain.repository.ExportRepository
import com.kilo.videoeditor.domain.repository.MediaRepository
import com.kilo.videoeditor.domain.repository.TextOverlayRepository
import com.kilo.videoeditor.domain.repository.VideoProjectRepository
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoProjectRepositoryImpl @Inject constructor(
    private val db: VideoEditorDatabase,
) : VideoProjectRepository {

    private val dao = db.videoProjectDao()

    override fun getAllProjects(): Flow<List<VideoProject>> =
        dao.getAllProjects().map { list -> list.map { it.toDomain() } }

    override suspend fun getProject(id: Long): VideoProject? =
        dao.getProject(id)?.toDomain()

    override suspend fun insertProject(project: VideoProject): Long =
        dao.insertProject(project.toEntity())

    override suspend fun updateProject(project: VideoProject) =
        dao.updateProject(project.toEntity())

    override suspend fun deleteProject(id: Long) = dao.deleteProject(id)

    override suspend fun updateSettings(id: Long, settings: EditSettings) {
        db.editSettingsDao().upsertSettings(settings.toEntity(id))
    }
}

@Singleton
class ClipRepositoryImpl @Inject constructor(
    private val db: VideoEditorDatabase,
) : ClipRepository {

    private val dao = db.clipDao()

    override suspend fun getClipsForProject(projectId: Long): List<Clip> =
        dao.getClipsForProject(projectId).map { it.toDomain() }

    override suspend fun insertClip(clip: Clip): Long =
        dao.insertClip(clip.toEntity())

    override suspend fun deleteClip(clip: Clip) =
        dao.deleteClip(clip.toEntity())

    override suspend fun reorderClips(clips: List<Clip>) {
        clips.forEachIndexed { index, clip ->
            dao.updateOrder(clip.id, index)
        }
    }
}

@Singleton
class TextOverlayRepositoryImpl @Inject constructor(
    private val db: VideoEditorDatabase,
) : TextOverlayRepository {

    private val dao = db.textOverlayDao()

    override suspend fun getOverlaysForProject(projectId: Long): List<TextOverlay> =
        dao.getOverlaysForProject(projectId).map { it.toDomain() }

    override suspend fun insertOverlay(overlay: TextOverlay): Long =
        dao.insertOverlay(overlay.toEntity())

    override suspend fun updateOverlay(overlay: TextOverlay) =
        dao.updateOverlay(overlay.toEntity())

    override suspend fun deleteOverlay(overlay: TextOverlay) =
        dao.deleteOverlay(overlay.toEntity())
}

@Singleton
class AudioEffectRepositoryImpl @Inject constructor(
    private val db: VideoEditorDatabase,
) : AudioEffectRepository {

    private val dao = db.audioEffectDao()

    override suspend fun getEffectsForProject(projectId: Long): List<AudioEffect> =
        dao.getEffectsForProject(projectId).map { it.toDomain() }

    override suspend fun insertEffect(effect: AudioEffect): Long =
        dao.insertEffect(effect.toEntity())

    override suspend fun deleteEffect(effect: AudioEffect) =
        dao.deleteEffect(effect.toEntity())
}

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val context: Context,
) : MediaRepository {

    override suspend fun getVideos(): List<VideoProject> {
        val resolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
        )
        val videos = mutableListOf<VideoProject>()
        val cursor = resolver.query(uri, projection, null, null, null)
        cursor?.use {
            val idIdx = it.getColumnIndex(MediaStore.Video.Media._ID)
            val titleIdx = it.getColumnIndex(MediaStore.Video.Media.TITLE)
            val durIdx = it.getColumnIndex(MediaStore.Video.Media.DURATION)
            val wIdx = it.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val hIdx = it.getColumnIndex(MediaStore.Video.Media.HEIGHT)
            val sIdx = it.getColumnIndex(MediaStore.Video.Media.SIZE)
            while (it.moveToNext()) {
                val id = if (idIdx >= 0) it.getString(idIdx) else null ?: continue
                videos.add(
                    VideoProject(
                        id = id.hashCode().toLong(),
                        uri = "content://media/external/video/media/$id",
                        name = if (titleIdx >= 0) it.getString(titleIdx) ?: "Vídeo" else "Vídeo",
                        durationMs = if (durIdx >= 0) it.getLong(durIdx) else 0L,
                        width = if (wIdx >= 0) it.getInt(wIdx) else 0,
                        height = if (hIdx >= 0) it.getInt(hIdx) else 0,
                        sizeBytes = if (sIdx >= 0) it.getLong(sIdx) else 0L,
                    )
                )
            }
        }
        return videos
    }

    override suspend fun importVideo(uri: String): Result<VideoProject> {
        return runCatching {
            val queryUri = Uri.parse(uri)
            val cursor = context.contentResolver.query(queryUri, null, null, null, null)
            val project = cursor?.use {
                val titleIdx = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val durIdx = it.getColumnIndex(MediaStore.MediaColumns.DURATION)
                val sizeIdx = it.getColumnIndex(MediaStore.MediaColumns.SIZE)
                val wIdx = it.getColumnIndex(MediaStore.MediaColumns.WIDTH)
                val hIdx = it.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
                VideoProject(
                    id = uri.hashCode().toLong(),
                    uri = uri,
                    name = if (titleIdx >= 0) it.getString(titleIdx) ?: "Vídeo" else "Vídeo",
                    durationMs = if (durIdx >= 0) it.getLong(durIdx) else 0L,
                    width = if (wIdx >= 0) it.getInt(wIdx) else 0,
                    height = if (hIdx >= 0) it.getInt(hIdx) else 0,
                    sizeBytes = if (sizeIdx >= 0) it.getLong(sizeIdx) else 0L,
                )
            } ?: VideoProject(
                id = uri.hashCode().toLong(),
                uri = uri,
                name = "Vídeo",
                durationMs = 0,
                width = 0,
                height = 0,
                sizeBytes = 0L,
            )
            project
        }
    }
}

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val context: Context,
) : ExportRepository {

    private var isCancelled = false

    override suspend fun exportProject(
        projectId: Long,
        settings: EditSettings,
        onProgress: (Int) -> Unit,
    ): Result<String> {
        isCancelled = false
        return try {
            val outputDir = context.cacheDir
            val outputFile = java.io.File(outputDir, "export_${System.currentTimeMillis()}.mp4")
            val totalSteps = 10
            for (i in 0 until totalSteps) {
                if (isCancelled) {
                    return Result.failure(IllegalStateException("Exportação cancelada"))
                }
                kotlin.coroutines.delay(100)
                onProgress(((i + 1) * 100) / totalSteps)
            }
            outputFile.writeText("placeholder")
            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Log.e("ExportRepository", "export failed", e)
            Result.failure(e)
        }
    }

    override suspend fun cancelExport() {
        isCancelled = true
    }
}