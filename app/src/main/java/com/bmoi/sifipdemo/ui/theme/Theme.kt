package com.bmoi.sifipdemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BmoiLightColorScheme = lightColorScheme(
    primary = BmoiPurple,
    onPrimary = Color.White,
    primaryContainer = BmoiPurpleLight,
    onPrimaryContainer = BmoiPurpleDeep,
    secondary = BmoiCyan,
    onSecondary = Color.White,
    secondaryContainer = BmoiPurpleTint,
    onSecondaryContainer = BmoiPurpleDeep,
    tertiary = BmoiCyanDark,
    onTertiary = Color.White,
    background = BmoiBackground,
    onBackground = BmoiText,
    surface = Color.White,
    onSurface = BmoiText,
    surfaceVariant = BmoiPurpleTint,
    onSurfaceVariant = BmoiPurpleDeep,
    outline = BmoiBorder,
    error = StatusError,
    onError = Color.White,
)

@Composable
fun BmoiTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BmoiLightColorScheme,
        typography = BmoiTypography,
        content = content,
    )
}
