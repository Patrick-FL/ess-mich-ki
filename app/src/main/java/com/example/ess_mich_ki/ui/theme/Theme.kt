package com.example.ess_mich_ki.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B0B0E),
    onPrimaryContainer = Color.White,
    secondary = NothingSage,
    onSecondary = Color.White,
    tertiary = NothingYellow,
    onTertiary = Color.Black,
    background = NothingDarkBg,
    onBackground = NothingTextPrimary,
    surface = NothingSurface,
    onSurface = NothingTextPrimary,
    surfaceVariant = NothingSurfaceVariant,
    onSurfaceVariant = NothingTextSecondary,
    surfaceContainer = NothingSurface,
    surfaceContainerHigh = NothingSurfaceVariant,
    outline = NothingBorder,
    outlineVariant = Color(0x14FFFFFF),
    error = NothingRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NothingRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = NothingSage,
    onSecondary = Color.White,
    tertiary = NothingYellow,
    onTertiary = Color.Black,
    background = NothingLightBg,
    onBackground = NothingLightTextPrimary,
    surface = NothingLightSurface,
    onSurface = NothingLightTextPrimary,
    surfaceVariant = NothingLightSurfaceVariant,
    onSurfaceVariant = NothingLightTextSecondary,
    surfaceContainer = NothingLightSurface,
    surfaceContainerHigh = NothingLightSurfaceVariant,
    outline = NothingLightBorder,
    outlineVariant = Color(0x1A000000),
    error = NothingRed,
    onError = Color.White
)

@Composable
fun EssmichkiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
