package com.bmoi.sifipdemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BmoiLightColorScheme = lightColorScheme(
    primary = BmoiNavy,
    onPrimary = Color.White,
    primaryContainer = BmoiNavyLight,
    onPrimaryContainer = Color.White,
    secondary = BmoiOrange,
    onSecondary = Color.White,
    secondaryContainer = BmoiSand,
    onSecondaryContainer = BmoiNavyDark,
    tertiary = BmoiGold,
    onTertiary = Color.White,
    background = BmoiSurface,
    onBackground = BmoiNavyDark,
    surface = Color.White,
    onSurface = BmoiNavyDark,
    surfaceVariant = BmoiSurface,
    onSurfaceVariant = BmoiNavy,
    error = StatusError,
    onError = Color.White,
)

@Composable
fun BmoiTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // BMOI demo: always render the light-corporate scheme so the branding
    // stays consistent in front of the bank's stakeholders.
    MaterialTheme(
        colorScheme = BmoiLightColorScheme,
        typography = BmoiTypography,
        content = content,
    )
}
