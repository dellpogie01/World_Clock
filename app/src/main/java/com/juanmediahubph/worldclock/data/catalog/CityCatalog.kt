package com.juanmediahubph.worldclock.data.catalog

import com.juanmediahubph.worldclock.data.model.City

object CityCatalog {
    val ALL_CITIES = listOf(
        // Asia
        City("manila", "Manila", "Philippines", "Asia/Manila", "Asia"),
        City("tokyo", "Tokyo", "Japan", "Asia/Tokyo", "Asia"),
        City("singapore", "Singapore", "Singapore", "Asia/Singapore", "Asia"),
        City("seoul", "Seoul", "South Korea", "Asia/Seoul", "Asia"),
        City("hong_kong", "Hong Kong", "Hong Kong", "Asia/Hong_Kong", "Asia"),
        City("bangkok", "Bangkok", "Thailand", "Asia/Bangkok", "Asia"),
        City("dubai", "Dubai", "United Arab Emirates", "Asia/Dubai", "Asia"),
        City("beijing", "Beijing", "China", "Asia/Shanghai", "Asia"),
        City("shanghai", "Shanghai", "China", "Asia/Shanghai", "Asia"),
        City("mumbai", "Mumbai", "India", "Asia/Kolkata", "Asia"),
        City("new_delhi", "New Delhi", "India", "Asia/Kolkata", "Asia"),
        City("taipei", "Taipei", "Taiwan", "Asia/Taipei", "Asia"),
        City("jakarta", "Jakarta", "Indonesia", "Asia/Jakarta", "Asia"),
        City("kuala_lumpur", "Kuala Lumpur", "Malaysia", "Asia/Kuala_Lumpur", "Asia"),
        City("tokyo", "Tokyo", "Japan", "Asia/Tokyo", "Asia"),
        City("riyadh", "Riyadh", "Saudi Arabia", "Asia/Riyadh", "Asia"),
        City("doha", "Doha", "Qatar", "Asia/Qatar", "Asia"),
        City("tel_aviv", "Tel Aviv", "Israel", "Asia/Jerusalem", "Asia"),

        // Europe
        City("london", "London", "United Kingdom", "Europe/London", "Europe"),
        City("paris", "Paris", "France", "Europe/Paris", "Europe"),
        City("berlin", "Berlin", "Germany", "Europe/Berlin", "Europe"),
        City("rome", "Rome", "Italy", "Europe/Rome", "Europe"),
        City("madrid", "Madrid", "Spain", "Europe/Madrid", "Europe"),
        City("amsterdam", "Amsterdam", "Netherlands", "Europe/Amsterdam", "Europe"),
        City("zurich", "Zurich", "Switzerland", "Europe/Zurich", "Europe"),
        City("vienna", "Vienna", "Austria", "Europe/Vienna", "Europe"),
        City("stockholm", "Stockholm", "Sweden", "Europe/Stockholm", "Europe"),
        City("oslo", "Oslo", "Norway", "Europe/Oslo", "Europe"),
        City("copenhagen", "Copenhagen", "Denmark", "Europe/Copenhagen", "Europe"),
        City("brussels", "Brussels", "Belgium", "Europe/Brussels", "Europe"),
        City("athens", "Athens", "Greece", "Europe/Athens", "Europe"),
        City("moscow", "Moscow", "Russia", "Europe/Moscow", "Europe"),
        City("dublin", "Dublin", "Ireland", "Europe/Dublin", "Europe"),
        City("warsaw", "Warsaw", "Poland", "Europe/Warsaw", "Europe"),

        // North America
        City("new_york", "New York", "United States", "America/New_York", "North America"),
        City("los_angeles", "Los Angeles", "United States", "America/Los_Angeles", "North America"),
        City("chicago", "Chicago", "United States", "America/Chicago", "North America"),
        City("toronto", "Toronto", "Canada", "America/Toronto", "North America"),
        City("vancouver", "Vancouver", "Canada", "America/Vancouver", "North America"),
        City("mexico_city", "Mexico City", "Mexico", "America/Mexico_City", "North America"),
        City("miami", "Miami", "United States", "America/New_York", "North America"),
        City("san_francisco", "San Francisco", "United States", "America/Los_Angeles", "North America"),
        City("denver", "Denver", "United States", "America/Denver", "North America"),
        City("montreal", "Montreal", "Canada", "America/Toronto", "North America"),

        // South America
        City("sao_paulo", "Sao Paulo", "Brazil", "America/Sao_Paulo", "South America"),
        City("buenos_aires", "Buenos Aires", "Argentina", "America/Argentina/Buenos_Aires", "South America"),
        City("santiago", "Santiago", "Chile", "America/Santiago", "South America"),
        City("bogota", "Bogota", "Colombia", "America/Bogota", "South America"),
        City("lima", "Lima", "Peru", "America/Lima", "South America"),

        // Africa
        City("cairo", "Cairo", "Egypt", "Africa/Cairo", "Africa"),
        City("johannesburg", "Johannesburg", "South Africa", "Africa/Johannesburg", "Africa"),
        City("lagos", "Lagos", "Nigeria", "Africa/Lagos", "Africa"),
        City("nairobi", "Nairobi", "Kenya", "Africa/Nairobi", "Africa"),
        City("cape_town", "Cape Town", "South Africa", "Africa/Johannesburg", "Africa"),

        // Oceania
        City("sydney", "Sydney", "Australia", "Australia/Sydney", "Oceania"),
        City("melbourne", "Melbourne", "Australia", "Australia/Melbourne", "Oceania"),
        City("brisbane", "Brisbane", "Australia", "Australia/Brisbane", "Oceania"),
        City("auckland", "Auckland", "New Zealand", "Pacific/Auckland", "Oceania"),
        City("fiji", "Suva", "Fiji", "Pacific/Fiji", "Oceania")
    ).distinctBy { it.id }
}
