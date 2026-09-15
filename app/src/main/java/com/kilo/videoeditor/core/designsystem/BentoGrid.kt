package com.kilo.videoeditor.core.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.kilo.videoeditor.core.designsystem.VideoEditorRadius
import com.kilo.videoeditor.core.designsystem.VideoEditorSpacing

/**
 * Tamanho de célula do Bento Grid.
 */
enum class BentoCellSize {
    /** 1x1 célula */
    Small,
    /** 2x1 célula (largura dupla) */
    Medium,
    /** 3x1 célula (largura tripla) */
    Large,
    /** 2x2 célula (quadrada grande) */
    Square,
}

/**
 * Dados para uma célula do Bento Grid.
 */
data class BentoItem(
    val id: Long,
    val size: BentoCellSize = BentoCellSize.Medium,
)

/**
 * Grid Bento: layout responsivo com células de tamanhos variados (como o iOS Home Screen).
 *
 * Suporta seleção com feedback de elevação.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BentoGrid(
    items: List<BentoItem>,
    modifier: Modifier = Modifier,
    columns: Int = 4,
    cellSpacing: Dp = VideoEditorSpacing.md,
    contentPadding: PaddingValues = VideoEditorSpacing.md,
    selectedId: Long? = null,
    onItemSelect: (Long) -> Unit = {},
    cellContent: @Composable BoxScope.(BentoItem) -> Unit,
) {
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            val isSelected = item.id == selectedId
            val targetElevation by animateDpAsState(
                if (isSelected) 8.dp else 2.dp,
                label = "bentoElevation",
            )

            val shape = RoundedCornerShape(VideoEditorRadius.lg)
            Card(
                modifier = Modifier
                    .bentoCellSize(item, columns, cellSpacing)
                    .clip(shape)
                    .clickable { onItemSelect(item.id) },
                shape = shape,
                elevation = CardDefaults.cardElevation(targetElevation),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(VideoEditorRadius.sm),
                    content = { cellContent(item) },
                )
            }
        }
    }
}

/**
 * Aplica o tamanho da célula de acordo com o tipo BentoCellSize.
 */
private fun Modifier.bentoCellSize(
    item: BentoItem,
    columns: Int,
    spacing: Dp,
): Modifier {
    val span = when (item.size) {
        BentoCellSize.Small -> 1
        BentoCellSize.Medium -> 2
        BentoCellSize.Large -> 3
        BentoCellSize.Square -> 2
    }
    val fraction = span.coerceAtMost(columns).toDouble() / columns
    return this
        .fillMaxWidth(fraction = fraction)
        .then(
            if (item.size == BentoCellSize.Square) {
                Modifier.aspectRatio(1f)
            } else {
                Modifier
            }
        )
}