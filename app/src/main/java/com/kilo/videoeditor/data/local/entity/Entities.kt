package com.kilo.videoeditor.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity de projeto de vídeo.
 */
@Entity(
    tableName = "video_projects",
    indices = [Index(value = ["uri"], unique = true)],
)
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val uri: String,
    val name: String,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val thumbnailUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
)

/**
 * Entity de configurações de edição (1:1 com o projeto).
 */
@Entity(
    tableName = "edit_settings",
    foreignKeys = [
        ForeignKey(
            entity = VideoProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["projectId"], unique = true)],
)
data class EditSettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val brightness: Float = 1.0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val mute: Boolean = false,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val rotation: Int = 0,
    val filters: String = "", // VideoFilter separados por vírgula
)

/**
 * Entity de clipe.
 */
@Entity(
    tableName = "clips",
    foreignKeys = [
        ForeignKey(
            entity = VideoProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["projectId", "order"])],
)
data class ClipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val uri: String,
    val name: String,
    val durationMs: Long,
    val order: Int,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
)

/**
 * Entity de overlay de texto.
 */
@Entity(
    tableName = "text_overlays",
    foreignKeys = [
        ForeignKey(
            entity = VideoProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TextOverlayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val fontSize: Float = 48f,
    val color: Int = 0xFFFFFFFF,
    val font: String = "sans-serif",
)

/**
 * Entity de efeito de áudio.
 */
@Entity(
    tableName = "audio_effects",
    foreignKeys = [
        ForeignKey(
            entity = VideoProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AudioEffectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val type: String,
    val startMs: Long,
    val endMs: Long,
    val intensity: Float = 0.5f,
)