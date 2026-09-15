package com.kilo.videoeditor.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kilo.videoeditor.core.designsystem.*
import com.kilo.videoeditor.core.designsystem.VideoEditorRadius
import com.kilo.videoeditor.core.designsystem.VideoEditorSpacing
import com.kilo.videoeditor.domain.model.VideoProject
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onImport: () -> Unit,
    onProjectClick: (VideoProject) -> Unit,
    onProjectDelete: (VideoProject) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = "VideoEditor",
                actions = {
                    IconButton(onClick = onImport) {
                        Icon(Icons.Default.Add, contentDescription = "Importar")
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoading && state.projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.8f))
            }
        } else if (state.projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Nenhum vídeo importado. Toque em + para começar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = 2,
                modifier = Modifier.padding(padding),
                contentPadding = VideoEditorSpacing.md,
                verticalArrangement = Arrangement.spacedBy(VideoEditorSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(VideoEditorSpacing.md),
            ) {
                items(state.projects) { project ->
                    ProjectBentoCell(
                        project = project,
                        onClick = { onProjectClick(project) },
                        onDelete = { onProjectDelete(project) },
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectBentoCell(
    project: VideoProject,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(VideoEditorRadius.md)),
        ) {
            AsyncImage(
                model = project.thumbnailUri ?: project.uri,
                contentDescription = project.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Clear,
                                Color.Black.copy(alpha = 0.55f),
                            ),
                        )
                    ),
            )
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(VideoEditorRadius.sm),
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(VideoEditorRadius.xs),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}