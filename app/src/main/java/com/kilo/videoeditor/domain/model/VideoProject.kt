package com.kilo.videoeditor.domain.model

import java.io.File

/**
 * Domínio: vídeo importado para o app.
 */
data class VideoProject(
    val id: Long,
    val uri: String,
    val name: String,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val thumbnailUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(),
)

/**
 * Configurações de edição aplicadas a um vídeo.
 */
data class EditSettings(
    val brightness: Float = 1.0f,       // 0.0 - 2.0
    val contrast: Float = 1.0f,         // 0.0 - 2.0
    val saturation: Float = 1.0f,       // 0.0 - 2.0
    val speed: Float = 1.0f,            // 0.25 - 4.0
    val volume: Float = 1.0f,           // 0.0 - 1.0
    val mute: Boolean = false,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val rotation: Int = 0,              // 0, 90, 180, 270
    val filters: List<VideoFilter> = emptyList(),
)

/**
 * Filtros disponíveis.
 */
enum class VideoFilter {
    NONE,
    VINTAGE,
    BLACK_WHITE,
    SEPIA,
    CLARITY,
    VIBRANT,
    COOL,
    WARM,
}

/**
 * Clipe de vídeo composto por múltiplos arquivos.
 */
data class Clip(
    val id: Long,
    val projectId: Long,
    val uri: String,
    val name: String,
    val durationMs: Long,
    val order: Int,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
)

/**
 * Text overlay adicionado ao vídeo.
 */
data class TextOverlay(
    val id: Long,
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
 * Efeito de áudio.
 */
data class AudioEffect(
    val id: Long,
    val projectId: Long,
    val type: AudioEffectType,
    val startMs: Long,
    val endMs: Long,
    val intensity: Float = 0.5f,
)

enum class AudioEffectType {
    REVERB,
    ECHO,
    CHORUS,
    FLANGER,
    PITCH,
}

/**
 * Estado de exportação.
 */
sealed class ExportState {
    data object Idle : ExportState()
    data class Progress(val percent: Int) : ExportState()
    data class Success(val outputPath: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

/**
 * Estado da UI do editor.
 */
data class EditorUiState(
    val selectedProject: VideoProject? = null,
    val settings: EditSettings = EditSettings(),
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val isExporting: Boolean = false,
    val exportState: ExportState = ExportState.Idle,
    val clips: List<Clip> = emptyList(),
    val overlays: List<TextOverlay> = emptyList(),
    val effects: List<AudioEffect> = emptyList(),
)