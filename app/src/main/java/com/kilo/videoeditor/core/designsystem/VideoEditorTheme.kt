package com.kilo.videoeditor.core.designsystem

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kilo.videoeditor.R
object VideoEditorTheme {
    private val DarkFallback = ColorScheme(
        primary = Color(0xFF6750A4),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFDAD5FF),
        onPrimaryContainer = Color(0xFF231A4A),
        secondary = Color(0xFF625B71),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE4E0F9),
        onSecondaryContainer = Color(0xFF1E1B2E),
        tertiary = Color(0xFF7F5253),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFDAD4),
        onTertiaryContainer = Color(0xFF410002),
        background = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF1C1B1F),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4CE),
        surfaceContainer = Color(0xFF2B2930),
        surfaceContainerHigh = Color(0xFF36343B),
        surfaceContainerHighest = Color(0xFF413F46),
        error = Color(0xFFF2B8B8),
        onError = Color(0xFF690003),
        errorContainer = Color(0xFF930006),
        onErrorContainer = Color(0xFFFFDAD5),
        outline = Color(0xFF948F99),
        outlineVariant = Color(0xFF49454F),
        inverseSurface = Color(0xFFE6E1E5),
        inverseOnSurface = Color(0xFF313034),
        inversePrimary = Color(0xFFBFB2FF),
        scrim = Color(0xFF000000),
        surfaceTint = Color(0xFF6750A4),
    )

    /**
     * Devolve o esquema de cores: Material You dinâmico (12+) ou fallback.
     */
    @Composable
    fun colorScheme(dynamicColor: Boolean): ColorScheme {
        val context = LocalContext.current
        return if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicColorScheme(context)
        } else {
            DarkFallback
        }
    }

    /**
     * Configura o MaterialTheme com o esquema de cores.
     */
    @Composable
    fun VideoEditorTheme(
        dynamicColor: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        val colorScheme = colorScheme(dynamicColor)

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            shapes = Shapes(),
            content = content,
        )
    }
}

/**
 * Espaçamentos customizados do design system (Bento Grid).
 */
object VideoEditorSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

/**
 * Cantos arredondados customizados para o Liquid Glass.
 */
object VideoEditorRadius {
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val pill = 50
}