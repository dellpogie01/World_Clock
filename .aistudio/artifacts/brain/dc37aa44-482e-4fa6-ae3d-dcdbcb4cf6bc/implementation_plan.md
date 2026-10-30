# World Clock — Implementation Plan

A feature-rich, elegant, offline-first World Clock Android application built with Kotlin, Jetpack Compose, and Material 3, featuring a Modern Midnight Blue & Indigo aesthetic.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following choices were confirmed during the interactive planning session:
> - **Visual Theme**: Modern Midnight Blue and Indigo (deep midnight surfaces `#0B132B`, vibrant indigo primary `#4361EE`, and cyan accents).
> - **Feature Suite**: Full Suite including World Clock, City Search & Management, Time Zone Converter, Meeting Time Helper, and Settings.
> - **Initial City State**: Clean start with the user's local time zone; users can easily add cities from a comprehensive built-in catalog of 50+ world cities.

---

## 1. Overview & Core Concept

World Clock provides instant, highly accurate international time tracking without requiring internet connectivity or location permissions. It handles DST (Daylight Saving Time), complex time zone offsets, day changes ("Yesterday" / "Tomorrow"), and time differences relative to local time.

## 2. User Experience & Visual Design

- **Aesthetic**: Premium Dark / Midnight Blue mood with high-contrast digital typography, frosted glass M3 card surfaces (`tonalElevation`), and fluid animations.
- **Navigation**: Bottom Navigation Bar with 4 tabs:
  1. **Clock**: Main local clock + saved cities list.
  2. **Converter**: Source-to-destination time conversion with DST transition warnings.
  3. **Meeting Planner**: Multi-city timeline helper with color-coded working hours (Day, Evening, Night).
  4. **Settings**: 12/24-hour format, seconds toggle, date toggle, theme selection, and about.
- **Accessibility**: Scalable typography (`sp`), 48dp+ touch targets, and full content descriptions.

## 3. Key Product Decisions & Data Strategy

- **Offline-First Catalog**: A robust built-in IANA time zone catalog covering Asia, Europe, North America, South America, Africa, and Oceania.
- **Local Persistence**: JSON-backed local storage for saved cities, sort order, and user preferences using Kotlin Coroutines and Gson/Moshi.
- **Accurate Timekeeping**: Powered by `java.time` (`ZoneId`, `ZonedDateTime`, `ZoneRules`) guaranteeing correct historical and future DST offsets without relying on fixed UTC offsets.

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│               Jetpack Compose UI (M3)                  │
│  (WorldClockScreen, Converter, Meeting, Settings)      │
└──────────────────────────┬─────────────────────────────┘
                           │ State & Events
┌──────────────────────────▼─────────────────────────────┐
│                 WorldClockViewModel                    │
│     (StateFlow, 1-second ticker, timezone logic)       │
└──────────────────────────┬─────────────────────────────┘
                           │ Repository & Domain
┌──────────────────────────▼─────────────────────────────┐
│             WorldClockRepository & Storage             │
│      (Local JSON persistence for cities & settings)    │
└────────────────────────────────────────────────────────┘
```

### Core Components & Modules
- `data/model/City.kt`: Represents a saved city with IANA zone ID.
- `data/catalog/CityCatalog.kt`: Comprehensive catalog of world cities.
- `domain/TimeCalculator.kt`: Precise `java.time` calculations for time differences, day shifts, and DST.
- `viewmodel/WorldClockViewModel.kt`: Central state management and 1-second tick loop.
- `ui/`: Compose screens and components.
- `test/`: Robolectric and Unit tests for time conversion and diff calculations.
