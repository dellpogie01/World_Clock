package com.juanmediahubph.worldclock.ui.cities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juanmediahubph.worldclock.data.catalog.CityCatalog
import com.juanmediahubph.worldclock.ui.theme.*
import com.juanmediahubph.worldclock.viewmodel.WorldClockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityScreen(viewModel: WorldClockViewModel, onNavigateBack: () -> Unit) {
    val savedCities by viewModel.savedCities.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = remember(searchQuery, savedCities) {
        val query = searchQuery.trim().lowercase()
        val savedIds = savedCities.map { it.id }.toSet()
        CityCatalog.ALL_CITIES.filter { city ->
            city.id !in savedIds && (
                city.name.lowercase().contains(query) || city.country.lowercase().contains(query) ||
                city.timeZoneId.lowercase().contains(query) || city.region.lowercase().contains(query))
        }
    }
    Scaffold(modifier = Modifier.imePadding(), topBar = {
        TopAppBar(title = { Text("Add City", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, "Back", Modifier.size(Sizes.icon))
            } })
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = Sizes.contentMax).fillMaxSize().padding(horizontal = Spacing.screen),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                OutlinedTextField(searchQuery, { searchQuery = it },
                    Modifier.fillMaxWidth().heightIn(min = Sizes.fieldMin),
                    label = { Text("Search") },
                    supportingText = { Text("Search by city, country or time zone...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, "Search", Modifier.size(Sizes.icon)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, "Clear search", Modifier.size(Sizes.icon))
                        }
                    }, shape = MaterialTheme.shapes.medium, singleLine = true)
                if (filteredCities.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No matching cities found or already added.", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(bottom = Spacing.screen),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        itemsIndexed(filteredCities, key = { index, city -> "${city.id}:$index" }) { _, city ->
                            Card(onClick = { viewModel.addCity(city); onNavigateBack() },
                                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low)) {
                                Row(Modifier.fillMaxWidth().heightIn(min = Sizes.rowMin).padding(Spacing.lg),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                                    Icon(Icons.Default.Public, "World clock", Modifier.size(Sizes.icon),
                                        tint = MaterialTheme.colorScheme.primary)
                                    Column(Modifier.weight(1f)) {
                                        Text(city.name, style = MaterialTheme.typography.titleMedium)
                                        Text("${city.country} • ${city.timeZoneId}", style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.addCity(city); onNavigateBack() }) {
                                        Icon(Icons.Default.Add, "Add ${city.name}", Modifier.size(Sizes.icon),
                                            tint = MaterialTheme.colorScheme.primary)
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
