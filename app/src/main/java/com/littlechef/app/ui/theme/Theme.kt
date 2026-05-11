package com.littlechef.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = default_accent_light,
    onPrimary = Color.White,
    primaryContainer = default_accent_light.copy(alpha = 0.2f),
    onPrimaryContainer = md_theme_light_onBackground,
    secondary = default_accent_light,
    onSecondary = Color.White,
    secondaryContainer = default_accent_light.copy(alpha = 0.2f),
    onSecondaryContainer = md_theme_light_onBackground,
    tertiary = default_accent_light,
    onTertiary = Color.White,
    tertiaryContainer = default_accent_light.copy(alpha = 0.2f),
    onTertiaryContainer = md_theme_light_onBackground,
    error = Color(0xFFBF2727),
    errorContainer = error_color,
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onBackground,
    surfaceVariant = md_theme_light_surface,
    onSurfaceVariant = md_theme_light_onBackgroundVariant,
    outline = Color(0xFF3A3A3A),
    inverseOnSurface = md_theme_light_background,
    inverseSurface = md_theme_light_onBackground,
    inversePrimary = default_accent_light,
    surfaceTint = default_accent_light,
    outlineVariant = Color(0xFF3A3A3A),
    scrim = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = default_accent_dark,
    onPrimary = Color.White,
    primaryContainer = default_accent_dark.copy(alpha = 0.2f),
    onPrimaryContainer = md_theme_dark_onBackground,
    secondary = default_accent_dark,
    onSecondary = Color.White,
    secondaryContainer = default_accent_dark.copy(alpha = 0.2f),
    onSecondaryContainer = md_theme_dark_onBackground,
    tertiary = default_accent_dark,
    onTertiary = Color.White,
    tertiaryContainer = default_accent_dark.copy(alpha = 0.2f),
    onTertiaryContainer = md_theme_dark_onBackground,
    error = Color(0xFFCE3737),
    errorContainer = error_color,
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onBackground,
    surfaceVariant = md_theme_dark_surface,
    onSurfaceVariant = md_theme_dark_onBackgroundVariant,
    outline = default_accent_dark,
    inverseOnSurface = md_theme_dark_background,
    inverseSurface = md_theme_dark_onBackground,
    inversePrimary = default_accent_dark,
    surfaceTint = default_accent_dark,
    outlineVariant = default_accent_dark,
    scrim = Color.Black,
)

// Custom shapes with rounded corners for text fields and other components
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LittleChefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use custom colors
    textScale: Float = 1.0f,
    fontFamily: FontFamily = FontFamily.Default,
    accentColor: androidx.compose.ui.graphics.Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }.let { scheme ->
        // Override primary/secondary/tertiary colors if accent color is provided
        if (accentColor != null) {
            scheme.copy(
                primary = accentColor,
                secondary = accentColor,
                tertiary = accentColor,
                primaryContainer = accentColor.copy(alpha = 0.2f),
                secondaryContainer = accentColor.copy(alpha = 0.2f),
                tertiaryContainer = accentColor.copy(alpha = 0.2f),
                // Don't tint surfaces with accent color - keep them neutral
                surfaceTint = Color.Transparent,
                inversePrimary = accentColor,
                outline = accentColor,
                outlineVariant = accentColor
            )
        } else {
            scheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Scale typography based on user preference and apply custom font
    // Also override base sizes for labelSmall, bodySmall, and titleSmall
    val scaledTypography = MaterialTheme.typography.let { typography ->
        typography.copy(
            displayLarge = typography.displayLarge.copy(fontSize = typography.displayLarge.fontSize * textScale, fontFamily = fontFamily),
            displayMedium = typography.displayMedium.copy(fontSize = typography.displayMedium.fontSize * textScale, fontFamily = fontFamily),
            displaySmall = typography.displaySmall.copy(fontSize = typography.displaySmall.fontSize * textScale, fontFamily = fontFamily),
            headlineLarge = typography.headlineLarge.copy(fontSize = typography.headlineLarge.fontSize * textScale, fontFamily = fontFamily),
            headlineMedium = typography.headlineMedium.copy(fontSize = typography.headlineMedium.fontSize * textScale, fontFamily = fontFamily),
            headlineSmall = typography.headlineSmall.copy(fontSize = typography.headlineSmall.fontSize * textScale, fontFamily = fontFamily),
            titleLarge = typography.titleLarge.copy(fontSize = typography.titleLarge.fontSize * textScale, fontFamily = fontFamily),
            titleMedium = typography.titleMedium.copy(fontSize = typography.titleMedium.fontSize * textScale, fontFamily = fontFamily),
            titleSmall = typography.titleSmall.copy(fontSize = 16.sp * textScale, fontFamily = fontFamily), // Changed from 14sp to 16sp
            bodyLarge = typography.bodyLarge.copy(fontSize = typography.bodyLarge.fontSize * textScale, fontFamily = fontFamily),
            bodyMedium = typography.bodyMedium.copy(fontSize = typography.bodyMedium.fontSize * textScale, fontFamily = fontFamily),
            bodySmall = typography.bodySmall.copy(fontSize = 14.sp * textScale, fontFamily = fontFamily), // Changed from 12sp to 14sp
            labelLarge = typography.labelLarge.copy(fontSize = 16.sp * textScale, fontFamily = fontFamily), // Changed from 14sp to 16sp
            labelMedium = typography.labelMedium.copy(fontSize = typography.labelMedium.fontSize * textScale, fontFamily = fontFamily),
            labelSmall = typography.labelSmall.copy(fontSize = 12.sp * textScale, fontFamily = fontFamily) // Changed from 11sp to 12sp
        )
    }

    // Disable overscroll/elastic animation
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
