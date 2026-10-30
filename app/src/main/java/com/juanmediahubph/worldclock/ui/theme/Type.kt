package com.juanmediahubph.worldclock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun type(size: Int, height: Int, weight: FontWeight = FontWeight.Normal, tracking: Double = 0.0) =
    TextStyle(fontFamily = FontFamily.Default, fontSize = size.sp, lineHeight = height.sp,
        fontWeight = weight, letterSpacing = tracking.sp)

val Typography = Typography(
    displayLarge = type(57, 64), displayMedium = type(45, 52), displaySmall = type(36, 44),
    headlineLarge = type(32, 40), headlineMedium = type(28, 36), headlineSmall = type(24, 32),
    titleLarge = type(22, 28), titleMedium = type(16, 24, FontWeight.Medium, 0.15),
    titleSmall = type(14, 20, FontWeight.Medium, 0.1),
    bodyLarge = type(16, 24, tracking = 0.5), bodyMedium = type(14, 20, tracking = 0.25),
    bodySmall = type(12, 16, tracking = 0.4),
    labelLarge = type(14, 20, FontWeight.Medium, 0.1),
    labelMedium = type(12, 16, FontWeight.Medium, 0.5),
    labelSmall = type(11, 16, FontWeight.Medium, 0.5)
)
