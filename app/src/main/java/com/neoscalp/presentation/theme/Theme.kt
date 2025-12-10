package com.neoscalp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeoScalpColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = NeonBlue,
    tertiary = NeonPurple,
    background = BackgroundPrimary,
    surface = Surface,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = NeonGreen,
    onSurface = NeonGreen,
    error = NeonRed,
    onError = Black
)

@Composable
fun NeoScalpTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeoScalpColorScheme,
        typography = Typography,
        content = content
    )
}
