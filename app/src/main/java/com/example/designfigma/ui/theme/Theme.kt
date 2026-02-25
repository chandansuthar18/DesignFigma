package com.example.designfigma.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DarkNavy,
    secondary = BrightOrange,
    tertiary = SoftBlue,
    background = White,
    surface = OffWhite,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkNavy,
    onSurface = DarkNavy
)

@Composable
fun DesignFigmaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}