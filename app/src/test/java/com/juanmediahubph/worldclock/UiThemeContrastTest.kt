package com.juanmediahubph.worldclock

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.juanmediahubph.worldclock.ui.theme.DarkColors
import com.juanmediahubph.worldclock.ui.theme.LightColors
import org.junit.Assert.assertTrue
import org.junit.Test

/** Protects semantic foreground/background contrast when palette tokens change. */
class UiThemeContrastTest {
    private fun contrast(a: Color, b: Color): Double {
        val first = a.luminance().toDouble()
        val second = b.luminance().toDouble()
        return (maxOf(first, second) + 0.05) / (minOf(first, second) + 0.05)
    }

    private fun verify(name: String, colors: ColorScheme) {
        val textPairs = listOf(
            "primary" to (colors.onPrimary to colors.primary),
            "primary container" to (colors.onPrimaryContainer to colors.primaryContainer),
            "secondary" to (colors.onSecondary to colors.secondary),
            "secondary container" to (colors.onSecondaryContainer to colors.secondaryContainer),
            "tertiary" to (colors.onTertiary to colors.tertiary),
            "tertiary container" to (colors.onTertiaryContainer to colors.tertiaryContainer),
            "background" to (colors.onBackground to colors.background),
            "surface" to (colors.onSurface to colors.surface),
            "cards" to (colors.onSurface to colors.surfaceContainerLow),
            "secondary card text" to (colors.onSurfaceVariant to colors.surfaceContainerLow),
            "secondary surface text" to (colors.onSurfaceVariant to colors.surface),
            "primary card text" to (colors.primary to colors.surfaceContainerLow),
            "error" to (colors.onError to colors.error),
            "error container" to (colors.onErrorContainer to colors.errorContainer),
            "snackbar" to (colors.inverseOnSurface to colors.inverseSurface)
        )
        textPairs.forEach { (role, pair) ->
            val ratio = contrast(pair.first, pair.second)
            assertTrue("$name $role contrast $ratio is below 4.5:1", ratio >= 4.5)
        }
        listOf(colors.outline, colors.primary, colors.tertiary).forEach { color ->
            val ratio = contrast(color, colors.surface)
            assertTrue("$name icon/border contrast $ratio is below 3:1", ratio >= 3.0)
        }
    }

    @Test fun lightThemeMeetsContrastTargets() = verify("light", LightColors)
    @Test fun darkThemeMeetsContrastTargets() = verify("dark", DarkColors)
}
