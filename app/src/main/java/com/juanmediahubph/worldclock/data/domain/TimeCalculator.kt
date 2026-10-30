package com.juanmediahubph.worldclock.data.domain

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class CityTimeInfo(
    val cityName: String,
    val country: String,
    val timeZoneId: String,
    val formattedTime: String,
    val formattedDate: String,
    val dayOfWeek: String,
    val timeDifferenceText: String,
    val dayIndicator: String?, // "Yesterday", "Tomorrow", or null if same day
    val zoneAbbreviation: String,
    val isNight: Boolean,
    val rawZonedDateTime: ZonedDateTime
)

object TimeCalculator {

    fun getCityTimeInfo(
        instant: Instant,
        localZoneId: ZoneId,
        targetZoneId: ZoneId,
        cityName: String,
        country: String,
        is24Hour: Boolean,
        showSeconds: Boolean
    ): CityTimeInfo {
        val localZdt = ZonedDateTime.ofInstant(instant, localZoneId)
        val targetZdt = ZonedDateTime.ofInstant(instant, targetZoneId)

        // Time format pattern
        val timePattern = buildString {
            append(if (is24Hour) "HH:mm" else "hh:mm")
            if (showSeconds) append(":ss")
            if (!is24Hour) append(" a")
        }
        val timeFormatter = DateTimeFormatter.ofPattern(timePattern, Locale.getDefault())
        val formattedTime = targetZdt.format(timeFormatter)

        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
        val formattedDate = targetZdt.format(dateFormatter)
        val dayOfWeek = targetZdt.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

        // Time difference relative to user local time
        val localOffset = localZdt.offset
        val targetOffset = targetZdt.offset
        val diffSeconds = targetOffset.totalSeconds - localOffset.totalSeconds
        val diffHours = diffSeconds / 3600.0

        val timeDifferenceText = when {
            diffHours == 0.0 -> "Same time as local"
            diffHours > 0 -> {
                val hours = diffHours.toInt()
                val mins = ((diffHours - hours) * 60).toInt()
                if (mins == 0) "$hours hr${if (hours > 1) "s" else ""} ahead"
                else "$hours hr ${mins}m ahead"
            }
            else -> {
                val absHours = kotlin.math.abs(diffHours).toInt()
                val mins = ((kotlin.math.abs(diffHours) - absHours) * 60).toInt()
                if (mins == 0) "$absHours hr${if (absHours > 1) "s" else ""} behind"
                else "$absHours hr ${mins}m behind"
            }
        }

        // Day indicator comparing dates
        val localDate = localZdt.toLocalDate()
        val targetDate = targetZdt.toLocalDate()
        val daysDiff = ChronoUnit.DAYS.between(localDate, targetDate)
        val dayIndicator = when {
            daysDiff > 0 -> "Tomorrow"
            daysDiff < 0 -> "Yesterday"
            else -> null
        }

        // Zone abbreviation (e.g. EST, PST, GMT, PHT)
        val zoneAbbreviation = targetZdt.format(DateTimeFormatter.ofPattern("z", Locale.getDefault()))

        // Night indicator (before 6 AM or after 6 PM)
        val hour = targetZdt.hour
        val isNight = hour < 6 || hour >= 19

        return CityTimeInfo(
            cityName = cityName,
            country = country,
            timeZoneId = targetZoneId.id,
            formattedTime = formattedTime,
            formattedDate = formattedDate,
            dayOfWeek = dayOfWeek,
            timeDifferenceText = timeDifferenceText,
            dayIndicator = dayIndicator,
            zoneAbbreviation = zoneAbbreviation,
            isNight = isNight,
            rawZonedDateTime = targetZdt
        )
    }
}
