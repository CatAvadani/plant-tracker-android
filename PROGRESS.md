# Plant Tracker Android - Progress Summary

## Project Overview
Plant Tracker Android is a calm plant-care app built with Jetpack Compose and Material 3. The project is currently in **Phase 2: Networking & Auth**. Authentication is connected to the deployed .NET API, while plant data screens still use local/static sample data until Phase 3.

## Current Status
- **Authentication**: Real login/register API calls are implemented.
- **Session handling**: JWT token and generated API key are stored securely and checked on splash.
- **Main app UI**: Home, Plants, Calendar, Settings, Plant Details, and Add Plant screens exist.
- **Plant data**: Still static/local through `fakePlants`.
- **Recent focus**: Auth screen redesign, Settings redesign, improved auth validation, clearer backend error messages, and safer logout handling.
- **Backend alignment**: Route casing fixes and display name persistence still need backend updates/redeployment.

## Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Navigation**: Navigation Compose
- **Icons**: Material Icons Extended
- **Networking**: Retrofit 2 and OkHttp 4
- **Image Loading**: Coil Compose
- **Security**: AndroidX Security Crypto with encrypted shared preferences
- **Concurrency**: Kotlin Coroutines
- **Architecture**: MVVM with Repository pattern

## Completed Tasks

### 1. Architecture & Structure
- Created a feature-oriented app structure:
  - `data/`: Auth repository, secure local token storage, network configuration, and API services.
  - `model/`: Shared plant model and local sample plant list.
  - `navigation/`: Route definitions, bottom-nav metadata, and `NavHost` setup.
  - `ui/components/`: Shared UI components for plant cards, bottom navigation, loading, and empty states.
  - `ui/screens/`: Auth, Home, Plants, Calendar, Settings, and secondary plant screens.

### 2. Navigation Flow
- Implemented app startup through `SplashScreen`.
- Splash checks for both JWT token and API key before routing to Home.
- Login routes to Home on successful authentication.
- Register routes back to Login on successful account creation.
- Settings logout clears local session data before navigating to Login.
- Bottom navigation supports Home, Plants, Calendar, and Settings.
- Secondary navigation supports Plant Details and Add Plant screens.

### 3. Authentication & Session Management
- Added `AuthApiService` for register, login, and API key generation.
- Added `AuthRepository` for authentication workflows.
- Added `AuthViewModel` with `Idle`, `Loading`, `Success`, and `Error` auth states.
- Added `TokenManager` using encrypted shared preferences for:
  - JWT token
  - API key
  - user email
  - display name
- Added `AuthInterceptor` to attach JWT and API key to authenticated requests.
- Login/register requests trim email input before submission.
- Backend error responses are parsed so UI can show messages such as `Invalid email or password.` instead of only HTTP codes.
- Logout clears encrypted preferences on `Dispatchers.IO`, then navigates after clearing completes.

### 3.1 Backend/API Notes
- The backend exposes endpoints that should be considered in later Android phases:
  - `POST /api/plants/image`: dedicated plant image upload endpoint for Phase 4.
  - `POST /api/auth/refresh`: token refresh endpoint to avoid forced logout when JWT expires.
  - `PATCH /api/users/me`: edit profile endpoint and likely path to repair/update display name data.
- Backend route casing is currently being normalized. Controllers/routes such as `/api/Plants`, `/api/Auth`, `/api/ApiKey`, and `/api/Users` should be lowercased and redeployed to Railway so the API contract is consistent with Android routes.
- Registration display name persistence is an open backend/data bug: new registrations currently store `NULL` for display name in the database. Android already sends `displayName` in `RegisterRequest`, but backend handling/database mapping still needs to be fixed and redeployed.
- `Plant.healthStatus` is an integer enum, not a free-form string:
  - `0 = Healthy`
  - `1 = Needs Attention`
  - `2 = Critical`
- The enum shape should drive the future Add/Edit Plant UI, likely with a picker/segmented control instead of a text field.

### 4. Auth UI Redesign
- Added `AuthDesign.kt` with shared auth UI primitives:
  - soft gradient background
  - plant-care brand mark using leaf and water-drop icons
  - shared auth card frame
  - styled text fields with leading icons
  - primary loading button
  - error message surface
- Redesigned `SplashScreen` with the new plant-care branding and loading state.
- Redesigned `LoginScreen` and `RegisterScreen` while preserving existing auth logic.
- Login button is disabled until email and password are non-blank.
- Register button is disabled until name, email, password are non-blank and password confirmation matches.

### 5. Main Screens & UI
- **Home**: Static dashboard summary and plant collection list.
- **Plants**: Static plant library list.
- **Plant Details**: Static detail view with care information and actions.
- **Add Plant**: Static form UI for plant creation, including placeholder image upload UI.
- **Calendar**: Static watering schedule/reminder screen.
- **Settings**: Redesigned profile/settings screen with:
  - profile summary card
  - preference toggles
  - account action rows
  - styled logout button
- Replaced the old flower icon in bottom navigation with the leaf-style `Eco` icon for the Plants tab.

### 6. Infrastructure & Build
- Added Retrofit, Gson converter, OkHttp, and logging interceptor.
- Added timeouts in `RetrofitInstance`.
- Added Coil dependency for future/available remote image loading.
- Added AndroidX Security Crypto for local sensitive data.
- Project currently builds successfully with:

```bash
./gradlew assembleDebug
```

## Project Structure
```text
app/src/main/java/com/catalina/planttracker/
├── data/
│   ├── auth/
│   │   └── AuthRepository.kt
│   ├── local/
│   │   └── TokenManager.kt
│   ├── model/
│   │   └── AuthModels.kt
│   └── network/
│       ├── ApiConfig.kt
│       ├── AuthApiService.kt
│       ├── AuthInterceptor.kt
│       └── RetrofitInstance.kt
├── model/
│   └── Plant.kt
├── navigation/
│   ├── Screen.kt
│   └── PlantNavGraph.kt
├── ui/
│   ├── auth/
│   │   └── AuthViewModel.kt
│   ├── components/
│   │   ├── BottomNavigationBar.kt
│   │   ├── PlantComponents.kt
│   │   └── StateComponents.kt
│   └── screens/
│       ├── auth/
│       │   ├── AuthDesign.kt
│       │   ├── SplashScreen.kt
│       │   ├── LoginScreen.kt
│       │   └── RegisterScreen.kt
│       ├── home/
│       │   └── HomeScreen.kt
│       ├── plants/
│       │   ├── PlantsScreen.kt
│       │   ├── PlantDetailsScreen.kt
│       │   └── AddPlantScreen.kt
│       ├── calendar/
│       │   └── CalendarScreen.kt
│       └── settings/
│           └── SettingsScreen.kt
└── MainActivity.kt
```

## Known Limitations
- Plant CRUD is not connected to the backend yet.
- Home, Plants, Plant Details, Add Plant, and Calendar still use static/sample data.
- Backend route casing fixes are still in progress and require Railway redeployment.
- New registrations currently persist `NULL` display names until the backend display name mapping is fixed.
- Edit Profile and Privacy Policy rows in Settings are placeholders.
- Dark Mode toggle is UI-only and does not yet change the app theme.
- Notifications toggle is UI-only and does not yet schedule reminders.
- Settings user info is read from local storage when the screen is composed; it is not reactive to profile edits yet.

## Next Steps

### Phase 3: Plants CRUD
- Create `PlantApiService`.
- Create `PlantRepository`.
- Implement `PlantViewModel`.
- Replace `fakePlants` with backend data.
- Connect Plants and Plant Details screens to real API responses.
- Connect Add Plant form to real create/update API calls.
- Model `healthStatus` as an integer enum in the Android UI and request/response models. ✓
- Add `CreatePlantRequest` and `UpdatePlantRequest` models. ✓
- Add loading, empty, and error states for plant screens.


### Phase 4: Images & Reminders
- Add real image picking.
- Add image upload support using `POST /api/plants/image`.
- Connect watering calendar to real reminder data.
- Implement local notifications or backend-driven reminders.

### Phase 5: Profile & Preferences
- Implement Edit Profile flow using `PATCH /api/users/me`.
- Make user profile data reactive.
- Add token refresh support using `POST /api/auth/refresh`.
- Persist theme preference.
- Implement notification preference persistence.
