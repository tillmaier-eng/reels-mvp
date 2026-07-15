package com.personal.reels.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ReelsDarkColors = darkColorScheme(
    primary = Color(0xFF00E676),      // neon green accent
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF14141A),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ReelsTheme(content: @Composable () -> Unit) {
    // Reels feeds are always full-bleed dark, regardless of system theme —
    // matching TikTok/Shorts/Reels convention keeps playback the visual focus.
    MaterialTheme(colorScheme = ReelsDarkColors, content = content)
}
