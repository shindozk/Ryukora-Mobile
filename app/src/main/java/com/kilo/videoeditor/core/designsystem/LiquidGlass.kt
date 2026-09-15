package com.kilo.videoeditor.core.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kilo.videoeditor.core.designsystem.VideoEditorRadius

/**
 * Liquid Glass: superfície translúcida com blur de fundo, contorno sutil e
 * brilho de borda. Inspirado no Material You / iOS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(VideoEditorRadius.lg),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f),
    blur: Float = 24f,
    border: BorderStroke? = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
    ),
    elevation: Dp = 0.dp,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val targetElevation = if (enabled) elevation else 0.dp
    val animatedElevation by animateDpAsState(targetElevation, label = "glassElevation")

    Card(
        modifier = modifier
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                ambientColor = Color.White.copy(alpha = 0.08f),
                spotColor = Color.White.copy(alpha = 0.12f),
            )
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Clear,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = border,
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(containerColor)
                .blur(blur)
                .drawWithContent {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.06f),
                        style = Stroke(
                            width = density * 1f,
                            cap = null,
                            pathEffect = null,
                        ),
                        size = size,
                    )
                    drawContent()
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(VideoEditorRadius.sm),
                content = content,
            )
        }
    }
}

/**
 * Versão genérica do Liquid Glass para composições arbitrárias.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(VideoEditorRadius.lg),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f),
    blur: Float = 24f,
    border: BorderStroke? = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
    ),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .blur(blur)
            .drawWithContent {
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.06f),
                    style = Stroke(
                        width = density * 1f,
                        cap = null,
                        pathEffect = null,
                    ),
                    size = size,
                )
                drawContent()
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = 0.08f),
                        ),
                    )
                )
        )
        content()
    }
}