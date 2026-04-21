package com.aarw.dexanalyze.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiary = DarkOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = Error,
    errorContainer = ErrorContainer,
    onError = OnError
)

private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    primary = LightPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiary = LightOnTertiary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = Error,
    errorContainer = ErrorContainer,
    onError = OnError
)

@Composable
fun DEXAnalyzeTheme(isDarkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
