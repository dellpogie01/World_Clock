# UI/UX audit — before changes

Scope: all Compose UI under `ui/`, MainActivity, manifest, XML launch themes, launcher resources, and persistence boundaries. Active screens are Home, Search, Favorites, Preference, and the timed splash. AddCityScreen and CityCard are retained legacy UI. No XML View layouts are used. Baseline lint: 0 errors, 58 warnings.

## Typography inventory

- Type.kt: headlineMedium 23/28sp; titleLarge 18/24; titleMedium 14/20; titleSmall 13/18; bodyLarge 14/20; bodyMedium 12/18; bodySmall 11/16; labelLarge 12/18; labelMedium 11/16; labelSmall 10/14. All are ad hoc; bodyMedium/bodySmall and labelSmall are too small. Display/headline roles are incomplete.
- WorldClockScreen: header title 26/32sp and subtitle 14/20sp; splash title 46/56sp and subtitle 24/32sp; preference options 18/24sp. Splash and header dominate small screens. SingleLineHeading creates TextStyle dynamically from 16sp down to 6sp in 0.25sp steps: illegible at small widths and defeats 200% font scaling.
- Legacy CityCard: LOCAL and day badges explicitly 10sp, below minimum. City/date/body labels inherit undersized bodySmall; time and city columns can collide.
- AddCityScreen: no raw text sizes, but inherits undersized subtitle styles. Redundant bodyLarge style expression and an unused Check import.

## Components and dimensions

- WorldClockScreen: 88dp header image, 368dp splash image, 92dp dial; 21dp navigation/favorite icons versus 20dp search icon. Switch slot manually forced to 60x48dp; labels are on the right, unlike the requested standard. No scale() is present. Card padding 14dp, radius 18dp. Search shape 14dp, header image shape 22dp, splash 64dp. Custom header shadow 6dp is excessive.
- Legacy CityCard: 16dp day/night icons, 18dp delete icon inside a 28dp IconButton (too small a target). Non-grid 6dp/2dp/1dp padding, 4dp badge radii. Tight two-column layout without room for long names or enlarged text.
- AddCityScreen: 12dp cards and text field, 16dp card padding; unweighted city column competes with add action. Field has only a placeholder. Card/action touch targets are implicit. No max content width.
- Analog strokes are 1/1.5/2/3dp and hub 3dp; these are drawing primitives, not touch targets. They should have named drawing tokens, not repeat in a screen.

## Color and contrast

- Color.kt defines hardcoded palette constants: midnight #0B132B/#1C2541/#233059, blue #4361EE/#4895EF, cyan #4CC9F0, white #F8F9FA/#FFFFFF, gray #8D99AE, and light #F0F4F8/#3F37C9. Several constants are unused; schemes leave tertiary, outline, error, inverse and container roles to unrelated defaults.
- Dark primary #4361EE against dark cards is weak for small labels; light onSurfaceVariant #8D99AE on #F0F4F8 is too low contrast. Light cards are pure white. Surface foreground roles are inconsistently inherited by text.
- Splash hardcodes #0B132B, white, and #ADB9D1 instead of theme roles. Dial border/ticks and dividers attenuate alpha without a contrast check.
- XML launch backgrounds match midnight and prevent an extra launcher image; preserve this behavior. Existing system-bar handling follows the selected theme and should remain.

## Spacing, shape, layout

- Repeated 3/6/10/14/18/22/40/64dp values mix multiple grids and corner systems. No Dimens.kt or Shapes.kt.
- Home/Search/Favorites headings are correctly fixed, but shrink-to-fit violates readability. Search box must stay fixed while results scroll.
- No tablet max width. Splash/header and analog rows risk clipping at 320dp/landscape/200% font size. Fixed splash proportions do not account for text taking more vertical space.
- Preferences scroll correctly but use custom control placement and no selected-row semantics. Fixed header + keyboard can crowd result space.

## UX and accessibility

- Existing favorite/search empty states exist and must remain; offline data is immediate, so there is no genuine loading state beyond the intentional splash. No error observable is exposed by the repository; do not invent error behavior or alter persistence.
- Favorite actions change star state immediately, but have no confirmation announcement/snackbar. Controls need clearer selected semantics and consistent focus/ripple states.
- Navigation/search icons have null descriptions. Heading semantics are missing. Analog clock has a useful description and is lifecycle-aware; preserve its animation and timing.
- AddCityScreen and CityCard are unused by current tabs, but must be kept and polished. The legacy catalog contains duplicate city IDs; changing catalog/business data is outside this presentation pass.

## Planned resolution and trade-offs

Centralize full light/dark schemes, M3 type scale, 4dp spacing, sizes, elevations, motion, and shapes. Keep tab names, all existing strings, splash duration, favorite operations, search predicates, settings persistence, package/app ID, dependencies, and resource assets. Use M3 top app bar, navigation, cards, controls, and snackbar feedback. Keep pinned titles/search; allow long titles to wrap instead of shrinking below 11sp. This deliberately supersedes the earlier one-line request to satisfy the latest 200% font-scale requirement. Switches remain mutually exclusive choices, with labels left and default-size controls right. No source control repository is present, so stages will be build-verified without creating commits.

## Resolution record

- Complete light/dark ocean-blue/sea-green schemes, including container, inverse, error, outline and surface roles; contrast regression tests cover text roles at 4.5:1 and icons/borders at 3:1.
- Full requested M3 type scale with line heights/tracking. Body content uses bodyMedium/bodyLarge (14/16sp); compact labels never below 11sp. Removed shrink-to-6sp headings and raw screen text sizes.
- Added Dimens.kt and Shapes.kt. Screen padding 16dp, 4dp spacing grid, 8/12/16/28dp corners, 1–3dp elevations, 24dp icons, 48dp hit targets, 56dp controls/rows, 600dp content limit, named clock drawing and motion tokens.
- Active tabs and retained legacy UI now consume shared tokens. Default-size switches are trailing controls in whole-row accessible switch targets. Original mutually exclusive setting updates remain unchanged.
- M3 pinned top app bar, navigation bar/compact navigation rail, cards, text fields, snackbar host, and buttons. Existing strings and artwork remain. Added day/night status glyphs from existing clock state.
- Clock layouts stack at narrow widths/large text. Portrait headings wrap, compact headings can scroll horizontally. Compact landscape places heading and search alongside each other. Navigation labels become accessible icon-only items above 130% text to avoid broken words; short landscape uses a rail. Keyboard temporarily hides navigation and compacts branding so input stays visible.
- Legacy AddCityScreen retains its filtering/add/back behavior; unique composition keys prevent duplicate legacy catalog IDs from crashing its lazy list without modifying catalog data. Delete targets in CityCard are now at least 48dp.
- Original splash duration, JSON persistence, catalog contents, time calculations, selected tab strings, and package/build identity are unchanged. No dependency changes.
- Intentional exceptions: no fake loading/error states for synchronous offline data; repository errors remain outside presentation scope. XML launch resources and existing launcher assets are retained. See CHANGELOG.md for verification, emulator findings, and manual review items.

Runtime follow-up: all four active tabs were exercised in dark mode, small/large text and keyboard layouts checked on API 36.1, and tablet/landscape constraints verified. Three later cold launches reached Home with no additional ANR; temporary emulator settings/test data were restored. Final build, eight tests and lint pass; warning count remains 58, matching baseline. See CHANGELOG.md for trade-offs and the initial environment-induced startup event.
