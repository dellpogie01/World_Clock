package com.juanmediahubph.worldclock.data.model

import java.io.Serializable

data class City(
    val id: String,
    val name: String,
    val country: String,
    val timeZoneId: String,
    val region: String
) : Serializable
