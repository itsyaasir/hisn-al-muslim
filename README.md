<div align="center">

# Hisnul Muslim

## About

Hisnul Muslim is an offline-first Android app for reading and revisiting adhkar from *Hisnul Muslim*.

Built with Jetpack Compose and Material 3, it focuses on calm reading, Arabic typography, collection-based navigation, and customizable reading settings.

</div>

## Features

- Collection-based home screen
- Swipeable dhikr reader inside each collection
- Arabic font selection
- Transliteration, translation, and reference controls
- Favorites and search
- Dynamic color, black theme, and appearance settings
- Offline bundled dataset

## Build

### Debug

```bash
./gradlew :app:installDebug
```

Or:

```bash
./gradlew :app:assembleDebug
```

Debug APK output:

`app/build/outputs/apk/debug/app-debug.apk`

### Release

```bash
./gradlew :app:assembleRelease
```

Release APK output:

`app/build/outputs/apk/release/app-release.apk`

> [!NOTE]
> Release installation requires proper signing configuration if you want to install it as a real release build.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
- DataStore
- Hilt

## Requirements

- Android Studio with Android SDK
- JDK 17
- Android `minSdk 24`

## Project Structure

- `app/src/main/java/com/example/hisnulmuslim/app` - app shell and startup
- `app/src/main/java/com/example/hisnulmuslim/feature` - screens and view models
- `app/src/main/java/com/example/hisnulmuslim/data` - Room, seed import, repositories
- `app/src/main/java/com/example/hisnulmuslim/core` - models, design system, utilities
- `app/src/main/assets` - bundled seed dataset

## Data

The app ships with a local seed dataset and does not require a runtime API connection.
