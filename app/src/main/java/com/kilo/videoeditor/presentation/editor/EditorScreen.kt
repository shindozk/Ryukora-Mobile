package com.kilo.videoeditor.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kilo.videoeditor.core.designsystem.*
import com.kilo.videoeditor.core.designsystem.VideoEditorRadius
import com.kilo.videoeditor.core.designsystem.VideoEditorSpacing
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.EditorUiState
import com.kilo.videoeditor.domain.model.ExportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    state: EditorUiState,
    onSettingsChange: (EditSettings) -> Unit,
    onPlayPause: () -> Unit,
    onExport: () -> Unit,
    onCancelExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val project = state.project
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = project?.name ?: "Editor",
                actions = {
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Exportar")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(VideoEditorSpacing.md),
            ) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxSize(),
                    elevation = 6.dp,
                ) {
                    project?.let {
                        AsyncImage(
                            model = it.thumbnailUri ?: it.uri,
                            contentDescription = it.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VideoEditorSpacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pausar" else "Reproduzir",
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(VideoEditorSpacing.md))

            AdjustSlider(
                label = "Brilho",
                value = state.settings.brightness,
                onValueChange = { onSettingsChange(state.settings.copy(brightness = it)) },
                valueRange = 0f..2f,
            )
            AdjustSlider(
                label = "Contraste",
                value = state.settings.contrast,
                onValueChange = { onSettingsChange(state.settings.copy(contrast = it)) },
                valueRange = 0f..2f,
            )
            AdjustSlider(
                label = "Saturação",
                value = state.settings.saturation,
                onValueChange = { onSettingsChange(state.settings.copy(saturation = it)) },
                valueRange = 0f..2f,
            )
            AdjustSlider(
                label = "Velocidade",
                value = state.settings.speed,
                onValueChange = { onSettingsChange(state.settings.copy(speed = it)) },
                valueRange = 0.25f..4f,
            )

            Spacer(modifier = Modifier.height(VideoEditorSpacing.xl))

            when (val export = state.exportState) {
                is ExportState.Progress -> {
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = VideoEditorSpacing.md),
                    ) {
                        Column(
                            modifier = Modifier.padding(VideoEditorRadius.sm),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "Exportando... ${export.percent}%",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            LinearProgressIndicator(
                                progress = export.percent / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = VideoEditorSpacing.sm),
                            )
                            TextButton(onClick = onCancelExport) {
                                Text("Cancelamento")
                            }
                        }
                    }
                }
                is ExportState.Success -> {
                    Text(
                        "Exportado: ${export.outputPath}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(VideoEditorSpacing.md),
                    )
                }
                is ExportState.Error -> {
                    Text(
                        "Erro: ${export.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(VideoEditorSpacing.md),
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun AdjustSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = VideoEditorSpacing.md, vertical = VideoEditorSpacing.xs),
    ) {
        Column(
            modifier = Modifier.padding(VideoEditorRadius.sm),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
            )
        }
    }
}