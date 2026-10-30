package com.juanmediahubph.worldclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juanmediahubph.worldclock.ui.clock.WorldClockScreen
import com.juanmediahubph.worldclock.ui.theme.WorldClockTheme
import com.juanmediahubph.worldclock.viewmodel.WorldClockViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WorldClockViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val dark = when (settings.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            WorldClockTheme(darkTheme = dark) { WorldClockScreen(viewModel, dark) }
        }
    }
}
