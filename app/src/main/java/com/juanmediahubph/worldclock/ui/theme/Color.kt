package com.juanmediahubph.worldclock.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val DarkColors = darkColorScheme(
    primary = Color(0xFFADC7FF), onPrimary = Color(0xFF002F65),
    primaryContainer = Color(0xFF17457F), onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFB8C7E2), onSecondary = Color(0xFF233248),
    secondaryContainer = Color(0xFF3A4960), onSecondaryContainer = Color(0xFFD8E3F8),
    tertiary = Color(0xFF80D5BC), onTertiary = Color(0xFF00382E),
    tertiaryContainer = Color(0xFF005144), onTertiaryContainer = Color(0xFFA2F2D8),
    background = Color(0xFF0E1520), onBackground = Color(0xFFE1E7F2),
    surface = Color(0xFF111821), onSurface = Color(0xFFE1E7F2),
    surfaceVariant = Color(0xFF414956), onSurfaceVariant = Color(0xFFC1C9D6),
    surfaceTint = Color(0xFFADC7FF), outline = Color(0xFF8B94A3), outlineVariant = Color(0xFF414956),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE1E7F2), inverseOnSurface = Color(0xFF2E3541),
    inversePrimary = Color(0xFF315DA8), scrim = Color(0xFF080F19),
    surfaceBright = Color(0xFF343C49), surfaceDim = Color(0xFF0E1520),
    surfaceContainerLowest = Color(0xFF09101A), surfaceContainerLow = Color(0xFF1A222E),
    surfaceContainer = Color(0xFF1E2633), surfaceContainerHigh = Color(0xFF28313F),
    surfaceContainerHighest = Color(0xFF333C4B)
)

// Swap the dark theme's foreground/surface pairs; keep the existing background colors.
val LightColors = DarkColors.copy(
    primary = DarkColors.onPrimary, onPrimary = DarkColors.primary,
    primaryContainer = DarkColors.onPrimaryContainer, onPrimaryContainer = DarkColors.primaryContainer,
    secondary = DarkColors.onSecondary, onSecondary = DarkColors.secondary,
    secondaryContainer = DarkColors.onSecondaryContainer, onSecondaryContainer = DarkColors.secondaryContainer,
    tertiary = DarkColors.onTertiary, onTertiary = DarkColors.tertiary,
    tertiaryContainer = DarkColors.onTertiaryContainer, onTertiaryContainer = DarkColors.tertiaryContainer,
    background = Color(0xFFF8F9FF), onBackground = Color(0xFF19202B),
    surface = DarkColors.onSurface, onSurface = DarkColors.surface,
    surfaceVariant = DarkColors.onSurfaceVariant, onSurfaceVariant = DarkColors.surfaceVariant,
    surfaceTint = DarkColors.onPrimary,
    outline = DarkColors.outlineVariant, outlineVariant = DarkColors.outline,
    error = DarkColors.onError, onError = DarkColors.error,
    errorContainer = DarkColors.onErrorContainer, onErrorContainer = DarkColors.errorContainer,
    inverseSurface = DarkColors.inverseOnSurface, inverseOnSurface = DarkColors.inverseSurface,
    inversePrimary = DarkColors.primary,
    surfaceBright = DarkColors.onSurface, surfaceDim = DarkColors.onSurfaceVariant,
    surfaceContainerLowest = DarkColors.onSurface, surfaceContainerLow = DarkColors.onSurface,
    surfaceContainer = DarkColors.onSurface, surfaceContainerHigh = DarkColors.onSurfaceVariant,
    surfaceContainerHighest = DarkColors.onSurfaceVariant
)
