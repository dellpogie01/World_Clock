package com.juanmediahubph.worldclock.data.repository

import android.content.Context
import com.juanmediahubph.worldclock.data.model.AppSettings
import com.juanmediahubph.worldclock.data.model.City
import com.juanmediahubph.worldclock.data.catalog.CityCatalog
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class WorldClockRepository(private val context: Context) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val citiesFile = File(context.filesDir, "saved_cities.json")
    private val settingsFile = File(context.filesDir, "app_settings.json")

    private val _savedCities = MutableStateFlow<List<City>>(loadSavedCities())
    val savedCities: StateFlow<List<City>> = _savedCities.asStateFlow()

    private val _settings = MutableStateFlow<AppSettings>(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun getSavedCitiesList(): List<City> = _savedCities.value

    fun getSettingsValue(): AppSettings = _settings.value

    fun saveCities(cities: List<City>) {
        _savedCities.value = cities
        try {
            val type = Types.newParameterizedType(List::class.java, City::class.java)
            val adapter = moshi.adapter<List<City>>(type)
            val json = adapter.toJson(cities)
            citiesFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveSettings(settings: AppSettings) {
        _settings.value = settings
        try {
            val adapter = moshi.adapter(AppSettings::class.java)
            val json = adapter.toJson(settings)
            settingsFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSavedCities(): List<City> {
        return try {
            if (citiesFile.exists()) {
                val json = citiesFile.readText()
                val type = Types.newParameterizedType(List::class.java, City::class.java)
                val adapter = moshi.adapter<List<City>>(type)
                adapter.fromJson(json) ?: emptyList()
            } else {
                emptyList() // Start with local time only per user preference
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadSettings(): AppSettings {
        return try {
            if (settingsFile.exists()) {
                val json = settingsFile.readText()
                val adapter = moshi.adapter(AppSettings::class.java)
                adapter.fromJson(json) ?: AppSettings()
            } else {
                AppSettings()
            }
        } catch (e: Exception) {
            AppSettings()
        }
    }
}
