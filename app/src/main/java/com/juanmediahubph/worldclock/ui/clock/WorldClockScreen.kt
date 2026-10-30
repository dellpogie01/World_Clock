package com.juanmediahubph.worldclock.ui.clock

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juanmediahubph.worldclock.R
import com.juanmediahubph.worldclock.data.catalog.CountryClockCatalog
import com.juanmediahubph.worldclock.data.domain.CityTimeInfo
import com.juanmediahubph.worldclock.data.domain.TimeCalculator
import com.juanmediahubph.worldclock.data.model.AppSettings
import com.juanmediahubph.worldclock.data.model.City
import com.juanmediahubph.worldclock.ui.components.ClockCard
import com.juanmediahubph.worldclock.ui.components.EmptyState
import com.juanmediahubph.worldclock.ui.components.PageHeading
import com.juanmediahubph.worldclock.ui.components.FittedSingleLineText
import com.juanmediahubph.worldclock.ui.components.WorldClockHeader
import com.juanmediahubph.worldclock.ui.theme.*
import com.juanmediahubph.worldclock.viewmodel.WorldClockViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@Composable
fun WorldClockScreen(viewModel: WorldClockViewModel, darkTheme: Boolean) {
    val instant by viewModel.currentInstant.collectAsStateWithLifecycle()
    val favorites by viewModel.savedCities.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf("Home") }
    var query by rememberSaveable { mutableStateOf("") }
    var splashComplete by rememberSaveable { mutableStateOf(false) }
    val splashDeadline = rememberSaveable { SystemClock.elapsedRealtime() + 3000L }
    val activity = LocalActivity.current as? ComponentActivity
    SideEffect {
        val darkBars = !splashComplete || darkTheme
        val style = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT) { darkBars }
        activity?.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
    }
    LaunchedEffect(Unit) {
        delay((splashDeadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L))
        splashComplete = true
    }
    if (!splashComplete) {
        SplashScreen()
        return
    }
    val clocks = remember { CountryClockCatalog.clocks }
    val results = remember(query, clocks) {
        clocks.filter { city -> query.isBlank() || listOf(city.country, city.name, city.timeZoneId)
            .any { it.contains(query.trim(), ignoreCase = true) } }
    }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    ClockDashboard(tab, { tab = it }, query, { query = it }, favorites, results, settings,
        viewModel.getLocalTimeInfo(instant, settings),
        viewModel::updateSettings, { viewModel.getCityTimeInfo(it, instant, settings) }, { city ->
            val saved = if (tab == "Search") favorites.firstOrNull {
                it.id == city.id || (it.country == city.country && it.timeZoneId == city.timeZoneId)
            } else city
            if (saved != null) viewModel.removeCity(saved.id) else viewModel.addCity(city)
            scope.launch {
                snackbar.currentSnackbarData?.dismiss()
                snackbar.showSnackbar(if (saved != null) "Removed ${city.name} from favorites" else "Added ${city.name} to favorites")
            }
        }, snackbar)
}

@Composable
private fun ClockDashboard(
    tab: String, onTab: (String) -> Unit, query: String, onQuery: (String) -> Unit,
    favorites: List<City>, results: List<City>, settings: AppSettings, localInfo: CityTimeInfo,
    onSettings: (AppSettings) -> Unit, cityInfo: (City) -> CityTimeInfo,
    onFavorite: (City) -> Unit, snackbar: SnackbarHostState
) {
    val largeText = LocalDensity.current.fontScale > Sizes.largeFontScale
    val tabs = listOf("Home" to Icons.Default.Home, "Search" to Icons.Default.Search,
        "Favorites" to Icons.Default.Star, "Preference" to Icons.Default.Tune)
    BoxWithConstraints(Modifier.fillMaxSize()) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val shortWindow = maxHeight < Sizes.compactHeight
    val compact = shortWindow || imeVisible
    val useRail = shortWindow && !imeVisible
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            if (!shortWindow || !imeVisible) Surface(shadowElevation = Elevation.low) { WorldClockHeader(compact) }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (!imeVisible && !useRail) {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = Elevation.none) {
                tabs.forEach { (label, icon) ->
                    NavigationBarItem(selected = tab == label, onClick = { onTab(label) },
                        icon = { Icon(icon, label, Modifier.size(Sizes.icon)) },
                        label = if (largeText) null else {{ Text(label, style = MaterialTheme.typography.labelSmall) }})
                }
            }
            }
        }
    ) { padding ->
        val homeScroll = rememberLazyListState()
        val searchScroll = rememberLazyListState()
        val favoriteScroll = rememberLazyListState()
        Row(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
        if (useRail) {
            NavigationRail(modifier = Modifier.fillMaxHeight(),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                windowInsets = WindowInsets(Spacing.none)) {
                tabs.forEach { (label, icon) ->
                    NavigationRailItem(selected = tab == label, onClick = { onTab(label) }, modifier = Modifier.weight(1f),
                        icon = { Icon(icon, label, Modifier.size(Sizes.icon)) })
                }
            }
        }
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
            if (tab == "Preference") {
                Preferences(settings, onSettings, Modifier.widthIn(max = Sizes.contentMax).fillMaxWidth())
            } else {
                Column(Modifier.widthIn(max = Sizes.contentMax).fillMaxSize()) {
                    if (tab == "Home" || tab == "Search") Surface(color = MaterialTheme.colorScheme.background) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = Spacing.screen, vertical = Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                            if (tab == "Home") FittedSingleLineText("Current Local Time Across Global Time Zones",
                                    MaterialTheme.typography.titleLarge,
                                    Modifier.fillMaxWidth().semantics { heading() })
                            else ClockSearchField(query, onQuery)
                        }
                    }
                    LazyColumn(Modifier.fillMaxWidth().weight(1f),
                        state = when (tab) { "Home" -> homeScroll; "Search" -> searchScroll; else -> favoriteScroll },
                        contentPadding = PaddingValues(Spacing.screen), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        if (tab == "Home") {
                            item(key = "local-time") {
                                ClockCard(localInfo, false, true, local = true, onFavorite = null)
                            }
                            item(key = "find-clock") {
                                FilledTonalButton(onClick = { onTab("Search") },
                                    modifier = Modifier.heightIn(min = Sizes.actionMin)) {
                                    Text("Find a Clock", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                        if (tab == "Search") {
                            items(results, key = { "search:${it.id}" }) { city ->
                                val saved = favorites.firstOrNull { it.id == city.id || (it.country == city.country && it.timeZoneId == city.timeZoneId) }
                                ClockCard(cityInfo(city), saved != null, false) { onFavorite(city) }
                            }
                            if (results.isEmpty()) item { EmptyState("No matching clocks", "Try another country, city, or time zone.") }
                        } else {
                            items(favorites, key = { "$tab:${it.id}" }) { city ->
                                ClockCard(cityInfo(city), true, tab == "Home") { onFavorite(city) }
                            }
                            if (favorites.isEmpty() && tab != "Home") item {
                                EmptyState("No favorites yet", "Find a place in Search and tap its star.")
                                FilledTonalButton(onClick = { onTab("Search") },
                                    modifier = Modifier.heightIn(min = Sizes.actionMin)) {
                                    Text("Find a Clock", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }
    }
}

@Composable
private fun ClockSearchField(query: String, onQuery: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(query, onQuery, modifier.fillMaxWidth().heightIn(min = Sizes.fieldMin),
        label = { FittedSingleLineText("Search a Country, City, or Time Zone", MaterialTheme.typography.bodyLarge) },
        leadingIcon = { Icon(Icons.Default.Search, "Search", Modifier.size(Sizes.icon)) },
        singleLine = true, shape = MaterialTheme.shapes.medium)
}

@Composable
private fun SplashScreen() {
    WorldClockTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().padding(Spacing.screen)) {
                val imageSize = minOf(Sizes.splashImage, maxWidth, maxHeight * 0.38f)
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight),
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.world_clock_artwork), "World Clock globe",
                        Modifier.size(imageSize).clip(MaterialTheme.shapes.extraLarge))
                    Spacer(Modifier.height(Spacing.xl))
                    Text("World Clock", style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(Spacing.sm))
                    Text("One world. Every moment.", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun Preferences(settings: AppSettings, onChange: (AppSettings) -> Unit, modifier: Modifier = Modifier) {
    val darkSelected = when (settings.themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    LazyColumn(modifier.fillMaxHeight(), contentPadding = PaddingValues(Spacing.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        item { PageHeading("Preference") }
        item { Text("Theme", style = MaterialTheme.typography.titleMedium) }
        item {
            Card(shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low)) {
                Column(Modifier.padding(Spacing.sm)) {
                    listOf("dark" to "Dark", "light" to "Light").forEach { (mode, label) ->
                        PreferenceOption(label, if (mode == "dark") darkSelected else !darkSelected) { onChange(settings.copy(themeMode = mode)) }
                    }
                }
            }
        }
        item { Text("Time format", style = MaterialTheme.typography.titleMedium) }
        item {
            Card(shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low)) {
                Column(Modifier.padding(Spacing.sm)) {
                    PreferenceOption("12-hour (AM / PM)", !settings.is24HourFormat) { onChange(settings.copy(is24HourFormat = false)) }
                    PreferenceOption("24-hour", settings.is24HourFormat) { onChange(settings.copy(is24HourFormat = true)) }
                }
            }
        }
    }
}

@Composable
private fun PreferenceOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = Sizes.rowMin).clip(MaterialTheme.shapes.medium)
        .toggleable(value = selected, role = Role.Switch, onValueChange = { onClick() })
        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        .animateContentSize(tween(Motion.short)), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = selected, onCheckedChange = null)
    }
}

private val previewCity = City("preview-manila", "Manila", "Philippines", "Asia/Manila", "Asia")
private fun previewInfo(city: City) = TimeCalculator.getCityTimeInfo(
    Instant.parse("2026-10-31T10:09:30Z"), ZoneId.of("UTC"), ZoneId.of(city.timeZoneId),
    city.name, city.country, false, true)

@Composable
private fun DashboardPreview(tab: String, dark: Boolean, empty: Boolean = false) {
    WorldClockTheme(darkTheme = dark) {
        ClockDashboard(tab, {}, "", {}, if (empty) emptyList() else listOf(previewCity),
            listOf(previewCity), AppSettings(), previewInfo(City("local", "Local Time", "Your Location", ZoneId.systemDefault().id, "Local")),
            {}, ::previewInfo, {}, remember { SnackbarHostState() })
    }
}

@Preview(name = "Home light", widthDp = 360, heightDp = 800)
@Composable
private fun HomeLightPreview() = DashboardPreview("Home", false)

@Preview(name = "Home dark", widthDp = 360, heightDp = 800)
@Composable
private fun HomeDarkPreview() = DashboardPreview("Home", true)

@Preview(name = "Small phone 200 percent", widthDp = 320, heightDp = 800, fontScale = 2f)
@Composable
private fun HomeLargeTextPreview() = DashboardPreview("Home", false)

@Preview(name = "Search light", widthDp = 360, heightDp = 800)
@Composable
private fun SearchLightPreview() = DashboardPreview("Search", false)

@Preview(name = "Search dark", widthDp = 360, heightDp = 800)
@Composable
private fun SearchDarkPreview() = DashboardPreview("Search", true)

@Preview(name = "Search 200 percent", widthDp = 320, heightDp = 800, fontScale = 2f)
@Composable
private fun SearchLargeTextPreview() = DashboardPreview("Search", false)

@Preview(name = "Favorites light", widthDp = 360, heightDp = 800)
@Composable
private fun FavoritesLightPreview() = DashboardPreview("Favorites", false)

@Preview(name = "Favorites dark", widthDp = 360, heightDp = 800)
@Composable
private fun FavoritesDarkPreview() = DashboardPreview("Favorites", true)

@Preview(name = "Preference light", widthDp = 360, heightDp = 800)
@Composable
private fun PreferencesLightPreview() = DashboardPreview("Preference", false)

@Preview(name = "Preference dark", widthDp = 360, heightDp = 800)
@Composable
private fun PreferencesDarkPreview() = DashboardPreview("Preference", true)

@Preview(name = "Preference 200 percent", widthDp = 320, heightDp = 800, fontScale = 2f)
@Composable
private fun PreferencesLargeTextPreview() = DashboardPreview("Preference", false)

@Preview(name = "Search landscape", widthDp = 800, heightDp = 360)
@Composable
private fun LandscapePreview() = DashboardPreview("Search", false)

@Preview(name = "Landscape 200 percent", widthDp = 800, heightDp = 360, fontScale = 2f)
@Composable
private fun LandscapeLargeTextPreview() = DashboardPreview("Search", true)

@Preview(name = "Tablet", widthDp = 840, heightDp = 1000)
@Composable
private fun TabletPreview() = DashboardPreview("Home", true)

@Preview(name = "Empty favorites", widthDp = 360, heightDp = 800)
@Composable
private fun EmptyPreview() = DashboardPreview("Favorites", false, empty = true)

@Preview(name = "Splash", widthDp = 360, heightDp = 800)
@Preview(name = "Splash landscape 200 percent", widthDp = 800, heightDp = 360, fontScale = 2f)
@Composable
private fun SplashPreview() = SplashScreen()
