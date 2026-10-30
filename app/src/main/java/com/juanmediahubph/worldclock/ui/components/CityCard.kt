package com.juanmediahubph.worldclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juanmediahubph.worldclock.data.domain.CityTimeInfo
import com.juanmediahubph.worldclock.ui.theme.*

/** Retained for existing callers; uses the same tokens as the active clock cards. */
@Composable
fun CityCard(
    timeInfo: CityTimeInfo,
    isLocal: Boolean = false,
    showDate: Boolean = true,
    onDelete: (() -> Unit)? = null
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = if (isLocal)
            MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low)) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Column(Modifier.weight(1f)) {
                    Text(timeInfo.cityName, style = MaterialTheme.typography.titleMedium)
                    Text(timeInfo.country, style = MaterialTheme.typography.bodyMedium,
                        color = if (isLocal) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(if (timeInfo.isNight) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    if (timeInfo.isNight) "Night time" else "Day time",
                    Modifier.size(Sizes.icon), tint = MaterialTheme.colorScheme.primary)
                if (onDelete != null) {
                    IconButton(onClick = onDelete,
                        modifier = Modifier.sizeIn(minWidth = Sizes.touchTarget, minHeight = Sizes.touchTarget)) {
                        Icon(Icons.Default.Delete, "Remove city", Modifier.size(Sizes.icon),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            if (isLocal) {
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary) {
                    Text("LOCAL", style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs))
                }
            }
            Text(timeInfo.formattedTime, style = MaterialTheme.typography.headlineMedium)
            if (showDate) Text("${timeInfo.dayOfWeek}, ${timeInfo.formattedDate}",
                style = MaterialTheme.typography.bodyMedium)
            Text(timeInfo.timeDifferenceText, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)
            if (timeInfo.dayIndicator != null) {
                Surface(shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
                    Text(timeInfo.dayIndicator, style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs))
                }
            }
            Text(timeInfo.zoneAbbreviation, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}
