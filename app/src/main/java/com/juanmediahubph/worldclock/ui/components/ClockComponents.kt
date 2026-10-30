package com.juanmediahubph.worldclock.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.juanmediahubph.worldclock.R
import com.juanmediahubph.worldclock.data.domain.CityTimeInfo
import com.juanmediahubph.worldclock.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorldClockHeader(compact: Boolean = false) {
    val largeText = LocalDensity.current.fontScale > Sizes.largeFontScale
    TopAppBar(
        title = {
            Row(Modifier.fillMaxWidth().widthIn(max = Sizes.contentMax),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Image(painterResource(R.drawable.world_clock_artwork), "World Clock globe",
                    Modifier.size(Sizes.featureIcon).clip(MaterialTheme.shapes.medium))
                Column(Modifier.weight(1f)) {
                    Text("World Clock", style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                        softWrap = false, modifier = Modifier.horizontalScroll(rememberScrollState()))
                    if (!compact) Text("One world. Every moment.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        expandedHeight = when { compact -> Sizes.toolbarCompact; largeText -> Sizes.toolbarLargeText; else -> Sizes.toolbar },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
internal fun PageHeading(title: String, compact: Boolean = false) {
    Text(title, style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth().semantics { heading() }
            .then(if (compact) Modifier.horizontalScroll(rememberScrollState()) else Modifier),
        softWrap = !compact)
}

/** Measures before drawing so the complete text fits one line without an ellipsis. */
@Composable
internal fun FittedSingleLineText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier) {
        val availableWidth = constraints.maxWidth
        val fittedStyle = remember(text, style, availableWidth, measurer) {
            if (availableWidth <= 0 || measurer.measure(text, style, softWrap = false, maxLines = 1).size.width <= availableWidth) {
                return@remember style
            }
            var lower = 0f
            var upper = 1f
            repeat(12) {
                val scale = (lower + upper) / 2f
                val candidate = style.copy(fontSize = style.fontSize * scale,
                    lineHeight = style.lineHeight * scale, letterSpacing = style.letterSpacing * scale)
                if (measurer.measure(text, candidate, softWrap = false, maxLines = 1).size.width <= availableWidth) {
                    lower = scale
                } else upper = scale
            }
            style.copy(fontSize = style.fontSize * lower, lineHeight = style.lineHeight * lower,
                letterSpacing = style.letterSpacing * lower)
        }
        Text(text, style = fittedStyle, maxLines = 1, softWrap = false)
    }
}

@Composable
internal fun EmptyState(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Icon(Icons.Default.Public, "World clock", Modifier.size(Sizes.featureIcon),
            tint = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun ClockCard(info: CityTimeInfo, favorite: Boolean, analog: Boolean,
                       local: Boolean = false, onFavorite: (() -> Unit)?) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low)) {
        Column(Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Icon(if (info.isNight) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    if (info.isNight) "Night time" else "Day time", Modifier.size(Sizes.icon),
                    tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f)) {
                    Text(if (local) info.cityName else info.country, style = MaterialTheme.typography.titleMedium)
                    Text(if (local) info.timeZoneId else info.cityName, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (onFavorite != null) IconToggleButton(favorite, { onFavorite() }, Modifier.sizeIn(minWidth = Sizes.touchTarget, minHeight = Sizes.touchTarget)) {
                    Icon(if (favorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        if (favorite) "Remove ${info.country} ${info.cityName} from favorites"
                        else "Favorite ${info.country} ${info.cityName}",
                        Modifier.size(Sizes.icon), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                ClockDetails(info, Modifier.weight(1f))
                if (analog) AnalogClock(info)
            }
            Text("${info.zoneAbbreviation} | UTC${info.rawZonedDateTime.offset.id.replace("Z", "+00:00")} | ${info.timeZoneId}",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ClockDetails(info: CityTimeInfo, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(info.formattedTime, style = MaterialTheme.typography.headlineMedium)
        Text(info.rawZonedDateTime.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.getDefault())),
            style = MaterialTheme.typography.bodyMedium)
        Text(info.timeDifferenceText, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun AnalogClock(info: CityTimeInfo) {
    val zone = remember(info.timeZoneId) { ZoneId.of(info.timeZoneId) }
    var now by remember { mutableStateOf(Instant.now()) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) withFrameNanos { now = Instant.now() }
        }
    }
    val colors = MaterialTheme.colorScheme
    Canvas(Modifier.size(Sizes.clock).semantics { contentDescription = "Live analog clock for ${info.cityName}" }) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(colors.surface, radius)
        drawCircle(colors.outline, radius - ClockDrawing.fine.toPx(), style = Stroke(ClockDrawing.fine.toPx()))
        fun point(angle: Double, length: Float): Offset {
            val radians = Math.toRadians(angle - 90)
            return center + Offset((cos(radians) * length).toFloat(), (sin(radians) * length).toFloat())
        }
        repeat(12) { tick ->
            drawLine(colors.onSurfaceVariant, point(tick * 30.0, radius * 0.80f),
                point(tick * 30.0, radius * 0.88f), ClockDrawing.tick.toPx(), StrokeCap.Round)
        }
        val time = now.atZone(zone)
        val seconds = time.second + time.nano / 1_000_000_000.0
        val minutes = time.minute + seconds / 60.0
        val hours = time.hour % 12 + minutes / 60.0
        drawLine(colors.onSurface, center, point(hours * 30, radius * 0.46f), ClockDrawing.hourHand.toPx(), StrokeCap.Round)
        drawLine(colors.onSurface, center, point(minutes * 6, radius * 0.67f), ClockDrawing.hand.toPx(), StrokeCap.Round)
        drawLine(colors.tertiary, point(seconds * 6 + 180, radius * 0.15f), point(seconds * 6, radius * 0.74f), ClockDrawing.fine.toPx(), StrokeCap.Round)
        drawCircle(colors.primary, ClockDrawing.hub.toPx(), center)
    }
}
