package com.example.eatmate.ui.theme

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

private val LightColors = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color(0xFF3D2E1F),
    primaryContainer = BrandPeach,
    onPrimaryContainer = Color(0xFF3D2E1F),

    secondary = BrandGreen,
    onSecondary = Color(0xFF3D2E1F),
    secondaryContainer = Color(0xFFE8F0D2),
    onSecondaryContainer = Color(0xFF3D2E1F),

    tertiary = BrandInfo,
    onTertiary = Color(0xFF1B3A52),

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,

    outline = OutlineLight,
    outlineVariant = Color(0xFFEFE3D6),

    error = DangerRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Color(0xFF3D2E1F),
    primaryContainer = Color(0xFF5C4322),
    onPrimaryContainer = BrandOrange,

    secondary = BrandGreen,
    onSecondary = Color(0xFF1F2E12),
    secondaryContainer = Color(0xFF2D3E1B),
    onSecondaryContainer = BrandGreen,

    tertiary = BrandInfo,
    onTertiary = Color(0xFF0D2740),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline = OutlineDark,
    outlineVariant = Color(0xFF3D2E20),

    error = DangerRed,
    onError = Color.White
)

@Composable
fun EatmateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EatmateTypography,
        shapes = EatmateShapes,
        content = content
    )
}
