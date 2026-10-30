package com.juanmediahubph.worldclock

import android.content.Context
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.juanmediahubph.worldclock.data.model.AppSettings
import com.juanmediahubph.worldclock.viewmodel.WorldClockViewModel
import kotlinx.coroutines.cancel
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `local clock follows changes to the device time zone`() {
    val originalZone = java.util.TimeZone.getDefault()
    val viewModel = WorldClockViewModel(ApplicationProvider.getApplicationContext<Application>())
    val instant = java.time.Instant.parse("2026-06-15T12:00:00Z")
    try {
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kathmandu"))
      val nepal = viewModel.getLocalTimeInfo(instant, AppSettings(is24HourFormat = true))
      assertEquals("Asia/Kathmandu", nepal.timeZoneId)
      assertEquals("17:45:00", nepal.formattedTime)
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/New_York"))
      val newYork = viewModel.getLocalTimeInfo(instant, AppSettings(is24HourFormat = true))
      assertEquals("America/New_York", newYork.timeZoneId)
      assertEquals("08:00:00", newYork.formattedTime)
      assertEquals("Same time as local", newYork.timeDifferenceText)
    } finally {
      java.util.TimeZone.setDefault(originalZone)
      viewModel.viewModelScope.cancel()
    }
  }

  @Test
  fun `renamed package resolves the launcher activity`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("com.juanmediahubph.worldclock", context.packageName)
    assertEquals(context.packageName, BuildConfig.APPLICATION_ID)
    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    org.junit.Assert.assertNotNull(launchIntent)
    assertEquals("com.juanmediahubph.worldclock.MainActivity", launchIntent?.component?.className)
    assertEquals(MainActivity::class.java.name, launchIntent?.component?.className)
  }

  @Test
  fun `country clocks cover every country with valid unique zones`() {
    val clocks = com.juanmediahubph.worldclock.data.catalog.CountryClockCatalog.clocks
    val regions = clocks.map { it.region }.toSet()
    org.junit.Assert.assertTrue(regions.containsAll(java.util.Locale.getISOCountries().toList()))
    assertEquals(clocks.size, clocks.map { it.id }.distinct().size)
    clocks.forEach { java.time.ZoneId.of(it.timeZoneId) }
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("World Clock", appName)
  }
}
