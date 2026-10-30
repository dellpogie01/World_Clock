# Changelog
## 2026-10-31 — Requested clock placement and theme controls

- Home analog clocks now sit to the right of the numeric time, date, and relative-time text. Existing clock sizes and typography are retained.
- Removed the Search and Favorites section headings. Search hint is now `Search a Country, City, or Time Zone` and retains its single-line fitting.
- Preference has a `Theme` section with Dark and Light choices. Light swaps the dark palette's foreground/surface and accent pairs while retaining the existing background colors and launcher artwork. Time-format controls and persisted settings structure are unchanged.
- Changed only `ClockComponents.kt`, `WorldClockScreen.kt`, and `Color.kt` for presentation. Debug build, all 10 unit tests, and lint pass (0 errors, 37 existing warnings). Verified all four tabs and Dark/Light selection on the emulator; evidence is in `output/requested-ui-adjustments/`. No physical phone was connected for installation.

## 2026-10-31 — Home local clock and single-line search

- Home always displays a live clock using the device's automatically selected time zone, followed by a `Find a Clock` button opening Search. Saved favorites remain below it; the local clock is not added to or removed from saved favorites.
- Pinned Home title is `Current Local Time Across Global Time Zones`, measured to fit one line without wrapping or ellipsis. The fitted text helper retains the theme's base typography and adjusts its size to available width.
- Search uses the single-line `Search Country, City, Time Zone` hint/floating label; the helper text below the field was removed.
- Build, all 10 unit tests, and lint pass (0 errors). Added regression coverage for device time-zone changes (Kathmandu and New York). Runtime verified at normal and 320dp widths, including focused search and the Home-to-Search button; screenshots are in `output/home-local-clock/`.
- Installed the verified APK on connected phone R94XB0341SH without opening/interacting with its foreground app. No permissions, dependencies, saved-data schema, package identity, or launcher assets changed.

## 2026-10-31 — Android package identity

- Application ID changed from `com.aistudio.worldclock.abxzqr` to `com.juanmediahubph.worldclock`; namespace changed from `com.example` to the same new ID.
- Main, unit-test, and instrumented-test Kotlin packages/imports and directory trees moved to `com/juanmediahubph/worldclock`. Launcher activity resolves to `com.juanmediahubph.worldclock.MainActivity`; generated R/BuildConfig and provider authorities follow the new identity.
- Added a launcher/package regression test and updated the installed-app context assertion. Application logic, JSON field names, signing, version, artwork, and dependencies are unchanged.
- Android treats the new ID as a separate application. Existing installs/data are not deleted or automatically migrated.
- Verification: a clean debug/app-test build, 9 unit tests, lint, and the emulator instrumented identity test (1 test) pass. Lint remains at 0 errors and 58 existing warnings. APK metadata and merged manifests confirm the new launcher and uniquely prefixed provider authorities. Old and new application IDs coexist on the emulator without install/provider conflicts; the new app reached Home and its process remained running with no observed new fatal exception or ANR. The emulator launcher wait initially timed out under load, but the subsequent layout confirmed Home was visible. Evidence is in `output/package-rename/`. Earlier UI audit/diagnostics retain their historical package names.
## Earlier UI polish

## Applied changes

| Files | Presentation changes |
| --- | --- |
| `ui/theme/Color.kt`, `Theme.kt` | Complete light/dark ocean-blue and sea-green schemes; soft surfaces, contrast-checked foregrounds, error/outline/container/inverse roles. Existing dynamic-color fallback retained. |
| `ui/theme/Type.kt` | Full M3 scale: 36sp displaySmall; 32/28/24sp headlines; 22/16/14sp titles; 16/14/12sp body; 14/12/11sp labels, with line heights/tracking. Body content uses at least 14sp. |
| New `ui/theme/Dimens.kt`, `Shapes.kt` | 4dp spacing grid, 16dp screen padding, 8/12/16/28dp corners, 1–3dp elevation, 24dp icons, 48dp touch targets, 56dp row/field minima, 600dp content limit, 200ms motion, named clock drawing tokens. |
| `ui/clock/WorldClockScreen.kt`, new `ui/components/ClockComponents.kt` | M3 app bar/navigation/cards/fields/snackbars; pinned headings/search; responsive clocks/splash; accessible setting rows; favorite confirmations; previews for all tabs in light/dark and large-text/landscape/tablet/empty states. |
| `ui/components/CityCard.kt`, `ui/cities/AddCityScreen.kt` | Retained legacy screens/APIs with wrapping, shared tokens and larger controls. Unique UI composition keys tolerate duplicate legacy catalog IDs without editing catalog data. |
| New `UiThemeContrastTest.kt`, `AUDIT.md` | Contrast regression tests and pre-edit audit with resolution record. |

## Verification

- Final `gradlew.bat --offline assembleDebug testDebugUnitTest lintDebug` passed using Android Studio's JDK. All **8 tests** pass. Lint: **0 errors, 58 existing warnings**, matching baseline.
- Tests check semantic text contrast >=4.5:1 and active icon/border contrast >=3:1 in both palettes. Static scans found no raw colors, dp/sp dimensions, `fontSize =`, or scale hacks in screen/component files.
- API 36.1 emulator checks: normal phone, 320dp/200% font, 800x320dp landscape/200% font, and 840dp tablet. Exercised all four dark-mode tabs, theme/system-bar contrast, 12/24-hour selection, search (`Taiwan` → Taipei), keyboard visibility, favorite add/remove/snackbar and empty-state recovery.
- Initial emulator startup under heavy system load hit an ANR during framework class loading. [ANR review](output/ui-polish/ANR-REVIEW.md) records evidence and attribution. Three subsequent cold launches reached Home without further observed crash/ANR (8255/8278/8811ms emulator TotalTime, including the intentional splash). Assess physical-device startup before release.
- Emulator resolution/density/font scale and test favorites/settings were restored. No connected physical phone was modified. Screenshots/evidence are in `output/ui-polish/`; some images show intermediate stages.

## Trade-offs and manual review

- No changes to MainActivity, ViewModel, repository, calculations, catalog data, schema/models, package/applicationId, tab keys/routes, dependencies, or existing artwork/resources. Existing JSON persistence and splash duration remain.
- Latest accessibility rules supersede earlier oversized/one-line branding: portrait headings wrap; compact headings scroll horizontally. Large text uses icon-only navigation with accessible destination descriptions; short landscape uses the same destinations in a Material rail. Keyboard input temporarily hides navigation and compacts branding. Review these adaptations.
- Switches remain mutually exclusive choices, using default M3 sizing and trailing controls. No fake loading/error states were added for immediate offline data or errors the repository does not expose.
- Existing dependency-update warnings and unused resources were retained to respect scope. No git repository exists; stages were independently built rather than committed.
- Manual checks still recommended: TalkBack/hardware focus, physical-device/OEM rendering, and the inactive legacy UI. Android Studio's preview renderer intermittently reported disposed-pointer/stale-class errors; refresh/rebuild previews if needed. Runtime emulator checks were used alongside successful previews.




