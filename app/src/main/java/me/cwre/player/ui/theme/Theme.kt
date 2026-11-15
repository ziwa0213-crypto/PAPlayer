package me.cwre.player.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun PaTheme(content: @Composable () -> Unit) {
    // 浅色主题还没做，先一律走深色，免得 Material3 默认配色漏进来。
    @Suppress("UNUSED_EXPRESSION")
    isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Palette.bg,
            surface = Palette.surface1,
            surfaceVariant = Palette.surface2,
            onBackground = Palette.text1,
            onSurface = Palette.text1,
            onSurfaceVariant = Palette.text2,
            outline = Palette.line,
            primary = Palette.accent,
            onPrimary = Palette.onAccent,
        ),
        content = content,
    )
}
