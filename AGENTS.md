# Leaf Care — Agent Guide

This file contains project-specific context for AI coding agents working on the Leaf Care Android application. Leaf Care is a plant tracking and care management app built with Jetpack Compose and Material 3, backed by a .NET Web API.

---

## Project Overview

- **Name**: Leaf Care (package `com.catalina.planttracker`)
- **Type**: Android application (minSdk 24, targetSdk 36, compileSdk 36)
- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose with Material 3
- **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Backend**: .NET Web API deployed on Railway (`https://planttracker-production-54c4.up.railway.app/`)
- **Current Phase**: Phase 3 — Plants CRUD (authentication and plant management are fully wired to the live API)

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose (BOM 2026.02.01), Material 3, Material Icons Extended |
| Navigation | Navigation Compose 2.7.7 |
| Architecture | MVVM with Repository pattern |
| Networking | Retrofit 2.11.0, OkHttp 4.12.0, Gson converter |
| Image Loading | Coil Compose 2.7.0 |
| Concurrency | Kotlin Coroutines 1.11.0, Flow |
| Security | AndroidX Security Crypto 1.1.0 (EncryptedSharedPreferences) |
| Testing | JUnit 4, AndroidX Test, Espresso, Compose UI Test |

---

## Build and Test Commands

All commands assume you are in the project root (`PlantTrackerAndroid/`).

```bash
# Build the debug APK
./gradlew :app:assembleDebug

# Run unit tests (JVM)
./gradlew :app:testDebugUnitTest

# Run instrumented tests (requires connected device or emulator)
./gradlew :app:connectedDebugAndroidTest

# Clean build artifacts
./gradlew clean

# Sync dependencies (or use Android Studio's "Sync Project with Gradle Files")
./gradlew --refresh-dependencies
```

- Gradle uses the version catalog at `gradle/libs.versions.toml` for dependency management.
- The project enforces `RepositoriesMode.FAIL_ON_PROJECT_REPOS` in `settings.gradle.kts`.
- Kotlin code style is set to `official` in `gradle.properties`.

---

## Project Structure

```
app/src/main/java/com/catalina/planttracker/
├── MainActivity.kt              # Entry point; initializes Retrofit and sets Compose content
├── data/
│   ├── auth/AuthRepository.kt
│   ├── carelogs/CareLogRepository.kt
│   ├── local/TokenManager.kt    # EncryptedSharedPreferences wrapper
│   ├── model/                   # DTOs: AuthModels, CareLogModels, PlantModels
│   ├── network/                 # ApiConfig, RetrofitInstance, AuthInterceptor, API services
│   └── plants/PlantRepository.kt
├── model/Plant.kt               # Domain model
├── navigation/
│   ├── PlantNavGraph.kt         # Single NavHost with all routes and bottom-bar logic
│   └── Screen.kt                # Sealed class of all screen routes + bottomNavItems list
├── ui/
│   ├── auth/AuthViewModel.kt
│   ├── carelogs/CareLogViewModel.kt
│   ├── plants/PlantViewModel.kt
│   ├── components/              # Reusable Composables: BottomNavigationBar, PlantComponents, StateComponents
│   ├── screens/                 # Feature screens grouped by folder
│   │   ├── auth/
│   │   ├── calendar/
│   │   ├── carelogs/
│   │   ├── home/
│   │   ├── plants/
│   │   └── settings/
│   └── theme/                   # Color.kt, Theme.kt, Type.kt (LeafCareTheme)
```

---

## Code Style Guidelines

Follow these conventions to stay consistent with the existing codebase:

1. **Language**: All code, comments, and documentation are written in **English**.
2. **Kotlin style**: Use the `official` Kotlin code style (enforced via `gradle.properties`).
3. **Package naming**: All lowercase, dot-separated (e.g., `com.catalina.planttracker.data.network`).
4. **UI State pattern**: ViewModels expose UI state via a sealed class with exactly these states:
   ```kotlin
   sealed class SomeUiState {
       object Idle : SomeUiState()
       object Loading : SomeUiState()
       data class Success(val data: T) : SomeUiState()
       data class Error(val message: String) : SomeUiState()
   }
   ```
   Use `MutableStateFlow<SomeUiState>` internally and expose `StateFlow<SomeUiState>` via `asStateFlow()`.
5. **ViewModel scope**: Launch coroutines with `viewModelScope.launch { ... }`.
6. **Repository results**: Repositories return `Result<T>` using `.onSuccess { }` / `.onFailure { }` at the call site.
7. **Screens**: Each screen is a `@Composable` function in its own file under `ui/screens/<feature>/`.
8. **Navigation**: Add new routes to the `Screen` sealed class in `navigation/Screen.kt`, then wire them into `PlantNavGraph.kt`. Bottom-nav items are listed in `bottomNavItems`.
9. **Comments**: Keep comments purposeful. Explain *why* for non-obvious logic; avoid redundant "what" comments.
10. **Imports**: No wildcard imports. Prefer explicit, fully qualified imports.
11. **String resources**: User-facing strings should live in `res/values/strings.xml`. Hard-coded strings are acceptable for debug logs or API constants only.

---

## Architecture Patterns

- **MVVM**: Each screen has a corresponding ViewModel. ViewModels hold UI state and business logic.
- **Repository pattern**: Data operations go through repositories (`AuthRepository`, `PlantRepository`, `CareLogRepository`). Repositories call API services and may cache results in memory.
- **Singleton Retrofit**: `RetrofitInstance` is a singleton initialized explicitly in `MainActivity.onCreate()`. API services are exposed as lazy properties.
- **Auth Interceptor**: `AuthInterceptor` attaches `Authorization: Bearer <JWT>` and `X-Api-Key` headers to all non-auth requests. On `401`, it clears the session and emits a `sessionExpired` event that triggers navigation to Login.
- **Token Manager**: `TokenManager` uses `EncryptedSharedPreferences` (AES256_GCM) to store the JWT token, API key, user email, and display name.
- **In-memory caching**: `PlantRepository` caches the plant list (`cachedPlants`) so tab switches do not trigger full reloads.

---

## Testing Strategy

The project currently has **only example/template tests**. When adding tests, use the following structure:

- **Unit tests**: `app/src/test/java/com/catalina/planttracker/`
  - Use JUnit 4 (`@Test`).
  - Test ViewModel logic, repository mapping, and utility functions.
- **Instrumented tests**: `app/src/androidTest/java/com/catalina/planttracker/`
  - Use `AndroidJUnit4` runner.
  - Use Compose UI Test (`composeTestRule`) for screen-level Compose assertions.
  - Use Espresso for non-Compose interactions if needed.

### Running tests
```bash
# Unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests (device/emulator required)
./gradlew :app:connectedDebugAndroidTest
```

---

## Security Considerations

1. **Encrypted storage**: JWT tokens and API keys are stored in `EncryptedSharedPreferences` via `TokenManager`. Never store credentials in plain `SharedPreferences`.
2. **API key header**: The backend expects an `X-Api-Key` header alongside the JWT for authenticated endpoints.
3. **AuthInterceptor**: Automatically handles token attachment and session expiration. Do not manually add auth headers in individual API calls.
4. **ProGuard**: Currently disabled (`isMinifyEnabled = false`). If enabled in the future, update `proguard-rules.pro` to keep Retrofit models and Compose classes.
5. **Network logging**: `HttpLoggingInterceptor` is active at `Level.BODY`. Be cautious with production builds; consider switching to `Level.HEADERS` or removing it for release.

---

## Key Files for Agents

| File | Purpose |
|------|---------|
| `gradle/libs.versions.toml` | Central dependency version catalog |
| `app/build.gradle.kts` | App-level build config (SDK versions, dependencies, Compose) |
| `app/src/main/java/.../data/network/ApiConfig.kt` | Base URL and timeout constants |
| `app/src/main/java/.../navigation/Screen.kt` | All navigation routes and bottom-nav definitions |
| `app/src/main/java/.../navigation/PlantNavGraph.kt` | NavHost setup; add new screens here |
| `app/src/main/java/.../data/local/TokenManager.kt` | Secure token storage and session expiration events |
| `app/src/main/java/.../data/network/AuthInterceptor.kt` | Automatic auth header injection and 401 handling |

---

## Current Limitations (as of latest commit)

- The backend occasionally returns `401 Authenticated user missing.` for `/api/plants`; this is a known backend/API-key association issue, not a client bug.
- The watering calendar uses a weekly visual placeholder; real reminder data is not yet connected.
- Edit Profile and Privacy Policy rows in Settings are non-functional placeholders.
- Dark mode toggle in Settings is UI-only and does not yet persist or override the system theme.
- Notifications toggle in Settings is UI-only and does not yet schedule real reminders.
- Edit Plant screen exists but may be partially implemented depending on the active branch.

---

## Roadmap

- **Phase 4**: Real image picking, multipart image upload (`POST /api/plants/image`), watering calendar connected to real reminders, local notifications.
- **Phase 5**: Edit Profile flow (`PATCH /api/users/me`), reactive user profile data, token refresh (`POST /api/auth/refresh`), persisted theme and notification preferences.
