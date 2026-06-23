package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val ColorBlockDark = Color(0xFF1E293B)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    secondary = SecondaryGreen,
    onSecondary = SurfaceWhite,
    background = BrandBackground,
    onBackground = DarkText,
    surface = SurfaceWhite,
    onSurface = DarkText,
    error = ErrorRed,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    secondary = SecondaryGreen,
    onSecondary = SurfaceWhite,
    background = DarkText,
    onBackground = BrandBackground,
    surface = ColorBlockDark,
    onSurface = BrandBackground,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
