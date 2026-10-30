package com.juanmediahubph.worldclock.data.model

data class AppSettings(
    val is24HourFormat: Boolean = false,
    val themeMode: String = "system", // "system", "light", "dark"
    val showSeconds: Boolean = true,
    val showDate: Boolean = true,
    val firstDayOfWeek: String = "Monday"
)
