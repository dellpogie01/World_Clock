# World Clock

Android application ID, namespace, and Kotlin base package: `com.juanmediahubph.worldclock`.
Launcher activity: `com.juanmediahubph.worldclock.MainActivity`.

This identity replaces the previous `com.aistudio.worldclock.abxzqr` application ID and `com.example` namespace. Android installs it as a separate app; existing installations and their data are retained, and data is not transferred automatically between application IDs.

Native Android app with Home, Search, Favorites, and Preference tabs. Home displays the automatically detected local time zone and saved clocks with live analog faces. Find a Clock opens Search. Search includes every country and its time zones; tap a star to save a favorite. Preferences include system/light/dark themes and 12/24-hour time. The fixed header uses the launcher artwork, and cold launches show a three-second branded splash. Existing saved cities and clock preferences are preserved.

## Local setup

1. Open this directory in Android Studio and use its bundled JDK.
2. Install Android SDK 36.1 and let Android Studio set `sdk.dir` in `local.properties`.
3. For a new local checkout, generate the debug key from PowerShell:

```powershell
& 'C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe' -genkeypair -keystore debug.keystore -storepass android -keypass android -alias androiddebugkey -dname 'CN=Android Debug,O=Android,C=US' -keyalg RSA -keysize 2048 -validity 10000
```

Keep an existing signing key when updating an installed app. The local debug key is ignored by version control. AI Studio signing configurations are preserved.

## Build and check

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
The clock features work offline and do not require a Gemini API key or Firebase configuration.
Release signing uses `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD`.

