package com.krittapas.tictactoe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColors = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    secondary = NeonCyan,
    tertiary = NeonMagenta,
    background = NeonBg,
    onBackground = NeonText,
    surface = NeonSurface,
    onSurface = NeonText,
    surfaceVariant = NeonSurface2,
    onSurfaceVariant = NeonTextDim,
    primaryContainer = NeonPurple,
    onPrimaryContainer = Color.White,
)

@Composable
fun TicTacToeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = GameColors, typography = Typography, content = content)
}