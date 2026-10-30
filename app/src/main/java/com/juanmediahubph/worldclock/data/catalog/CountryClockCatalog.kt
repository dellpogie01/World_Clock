package com.juanmediahubph.worldclock.data.catalog

import android.icu.util.TimeZone
import com.juanmediahubph.worldclock.data.model.City
import java.util.Locale

/** Country-to-zone mappings come from the device's IANA time zone database. */
object CountryClockCatalog {
    val clocks: List<City> by lazy {
        (Locale.getISOCountries().toList() + "XK").flatMap { code ->
            val country = if (code == "XK") "Kosovo" else Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.ENGLISH)
            val zones = TimeZone.getAvailableIDs(code).toList().ifEmpty {
                // Uninhabited territories without an assigned IANA zone.
                listOf(when (code) {
                    "BV" -> "Europe/Oslo"
                    "HM" -> "Indian/Kerguelen"
                    "XK" -> "Europe/Belgrade"
                    else -> "Etc/UTC"
                })
            }
            zones.map { TimeZone.getCanonicalID(it) ?: it }.distinct().map { zone ->
                City("country:$code:$zone", zone.substringAfterLast('/').replace('_', ' '),
                    country, zone, code)
            }
        }.sortedWith(compareBy<City> { it.country }.thenBy { it.timeZoneId })
    }
}
