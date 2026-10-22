package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmochiPrimary,
    onPrimary = EmochiOnPrimary,
    primaryContainer = EmochiPrimaryContainer,
    secondary = EmochiViolet,
    tertiary = EmochiCyan,
    background = EmochiBackground,
    surface = EmochiSurface,
    surfaceVariant = EmochiCard,
    onBackground = EmochiTextPrimary,
    onSurface = EmochiTextPrimary,
    onSurfaceVariant = EmochiTextSecondary,
    error = EmochiError,
    errorContainer = EmochiErrorContainer
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
