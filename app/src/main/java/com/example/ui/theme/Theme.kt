package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MahiDarkColorScheme = darkColorScheme(
    primary = MahiCyan,
    onPrimary = MahiObsidian,
    secondary = MahiMagenta,
    onSecondary = MahiObsidian,
    tertiary = MahiViolet,
    onTertiary = MahiTextPrimary,
    background = MahiObsidian,
    onBackground = MahiTextPrimary,
    surface = MahiSurface,
    onSurface = MahiTextPrimary,
    surfaceVariant = MahiSurfaceVariant,
    onSurfaceVariant = MahiTextSecondary,
    outline = MahiOutline,
    error = MahiRose,
    onError = MahiObsidian,
)

@Composable
fun MahiTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MahiDarkColorScheme,
        typography = Typography,
        content = content,
    )
}
