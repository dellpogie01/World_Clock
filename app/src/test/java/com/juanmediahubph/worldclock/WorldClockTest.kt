package com.juanmediahubph.worldclock

import com.juanmediahubph.worldclock.data.domain.TimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class WorldClockTest {

    @Test
    fun zoneAbbreviationReflectsDaylightSavingTime() {
        val previousLocale = java.util.Locale.getDefault()
        try {
            java.util.Locale.setDefault(java.util.Locale.US)
            fun abbreviation(instant: String) = TimeCalculator.getCityTimeInfo(
                Instant.parse(instant), ZoneId.of("UTC"), ZoneId.of("America/New_York"),
                "New York", "United States", true, false
            ).zoneAbbreviation
            assertEquals("EDT", abbreviation("2026-06-15T12:00:00Z"))
            assertEquals("EST", abbreviation("2026-01-15T12:00:00Z"))
        } finally {
            java.util.Locale.setDefault(previousLocale)
        }
    }

    @Test
    fun testTimeCalculation() {
        val instant = Instant.parse("2026-06-15T12:00:00Z")
        val localZone = ZoneId.of("UTC")
        val targetZone = ZoneId.of("Asia/Manila") // UTC+8

        val info = TimeCalculator.getCityTimeInfo(
            instant = instant,
            localZoneId = localZone,
            targetZoneId = targetZone,
            cityName = "Manila",
            country = "Philippines",
            is24Hour = true,
            showSeconds = false
        )

        assertEquals("Manila", info.cityName)
        assertEquals("20:00", info.formattedTime)
        assertEquals("8 hrs ahead", info.timeDifferenceText)
    }

    @Test
    fun testSameTimeZone() {
        val instant = Instant.parse("2026-06-15T12:00:00Z")
        val zone = ZoneId.of("Europe/London")

        val info = TimeCalculator.getCityTimeInfo(
            instant = instant,
            localZoneId = zone,
            targetZoneId = zone,
            cityName = "London",
            country = "United Kingdom",
            is24Hour = false,
            showSeconds = false
        )

        assertEquals("London", info.cityName)
        assertEquals("Same time as local", info.timeDifferenceText)
    }
}
