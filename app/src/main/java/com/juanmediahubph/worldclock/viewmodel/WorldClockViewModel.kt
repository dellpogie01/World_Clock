package com.juanmediahubph.worldclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juanmediahubph.worldclock.data.model.AppSettings
import com.juanmediahubph.worldclock.data.model.City
import com.juanmediahubph.worldclock.data.catalog.CityCatalog
import com.juanmediahubph.worldclock.data.repository.WorldClockRepository
import com.juanmediahubph.worldclock.data.domain.CityTimeInfo
import com.juanmediahubph.worldclock.data.domain.TimeCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WorldClockViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WorldClockRepository(application)

    private val _currentInstant = MutableStateFlow(Instant.now())
    val currentInstant: StateFlow<Instant> = _currentInstant.asStateFlow()

    val savedCities: StateFlow<List<City>> = repository.savedCities
    val settings: StateFlow<AppSettings> = repository.settings

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Converter state
    private val _converterSourceCity = MutableStateFlow<City?>(null)
    val converterSourceCity: StateFlow<City?> = _converterSourceCity.asStateFlow()

    private val _converterDestCity = MutableStateFlow<City?>(null)
    val converterDestCity: StateFlow<City?> = _converterDestCity.asStateFlow()

    private val _converterZonedDateTime = MutableStateFlow(ZonedDateTime.now())
    val converterZonedDateTime: StateFlow<ZonedDateTime> = _converterZonedDateTime.asStateFlow()

    // Meeting planner state
    private val _meetingSelectedCities = MutableStateFlow<List<City>>(emptyList())
    val meetingSelectedCities: StateFlow<List<City>> = _meetingSelectedCities.asStateFlow()

    private val _meetingHour = MutableStateFlow(ZonedDateTime.now().hour)
    val meetingHour: StateFlow<Int> = _meetingHour.asStateFlow()

    private val _meetingDayOffset = MutableStateFlow(0) // 0 for today, +1 for tomorrow
    val meetingDayOffset: StateFlow<Int> = _meetingDayOffset.asStateFlow()

    private var tickerJob: Job? = null

    init {
        startTicker()
        // Initialize default converter cities if available
        val cities = repository.getSavedCitiesList()
        if (cities.isNotEmpty()) {
            _converterSourceCity.value = cities.first()
            if (cities.size > 1) {
                _converterDestCity.value = cities[1]
            } else {
                _converterDestCity.value = CityCatalog.ALL_CITIES.firstOrNull { it.id != cities.first().id }
            }
            _meetingSelectedCities.value = cities.take(4)
        } else {
            // Default to local and Manila/New York
            _converterSourceCity.value = City("local", "Local Time", "Device Default", ZoneId.systemDefault().id, "Local")
            _converterDestCity.value = CityCatalog.ALL_CITIES.firstOrNull()
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                _currentInstant.value = Instant.now()
                delay(1000L)
            }
        }
    }

    fun addCity(city: City) {
        val current = repository.getSavedCitiesList().toMutableList()
        if (current.none { it.id == city.id }) {
            current.add(city)
            repository.saveCities(current)
            if (_meetingSelectedCities.value.isEmpty()) {
                _meetingSelectedCities.value = current.take(4)
            }
        }
    }

    fun removeCity(cityId: String) {
        val current = repository.getSavedCitiesList().toMutableList()
        current.removeAll { it.id == cityId }
        repository.saveCities(current)
        val meetingCurrent = _meetingSelectedCities.value.toMutableList()
        meetingCurrent.removeAll { it.id == cityId }
        _meetingSelectedCities.value = meetingCurrent
    }

    fun reorderCities(newIndicesList: List<City>) {
        repository.saveCities(newIndicesList)
    }

    fun updateSettings(newSettings: AppSettings) {
        repository.saveSettings(newSettings)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Converter actions
    fun setConverterSource(city: City?) {
        _converterSourceCity.value = city
    }

    fun setConverterDest(city: City?) {
        _converterDestCity.value = city
    }

    fun setConverterTime(zdt: ZonedDateTime) {
        _converterZonedDateTime.value = zdt
    }

    // Meeting planner actions
    fun toggleMeetingCity(city: City) {
        val current = _meetingSelectedCities.value.toMutableList()
        if (current.any { it.id == city.id }) {
            current.removeAll { it.id == city.id }
        } else {
            current.add(city)
        }
        _meetingSelectedCities.value = current
    }

    fun setMeetingHour(hour: Int) {
        _meetingHour.value = hour.coerceIn(0, 23)
    }

    fun setMeetingDayOffset(offset: Int) {
        _meetingDayOffset.value = offset
    }

    fun getLocalTimeInfo(instant: Instant, settings: AppSettings): CityTimeInfo {
        val systemZone = ZoneId.systemDefault()
        return TimeCalculator.getCityTimeInfo(
            instant = instant,
            localZoneId = systemZone,
            targetZoneId = systemZone,
            cityName = "Local Time",
            country = "Your Location",
            is24Hour = settings.is24HourFormat,
            showSeconds = settings.showSeconds
        )
    }

    fun getCityTimeInfo(city: City, instant: Instant, settings: AppSettings): CityTimeInfo {
        val systemZone = ZoneId.systemDefault()
        val targetZone = try {
            ZoneId.of(city.timeZoneId)
        } catch (e: Exception) {
            systemZone
        }
        return TimeCalculator.getCityTimeInfo(
            instant = instant,
            localZoneId = systemZone,
            targetZoneId = targetZone,
            cityName = city.name,
            country = city.country,
            is24Hour = settings.is24HourFormat,
            showSeconds = settings.showSeconds
        )
    }
}
