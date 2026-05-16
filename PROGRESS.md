# Leaf Care - Progress Summary

## Project Overview
Leaf Care is a calm plant-care app built with Jetpack Compose and Material 3. The project is currently in **Phase 3: Plants CRUD**. Authentication and plant management are connected to the deployed .NET API.

## Current Status
- **Authentication**: Real login/register API calls are implemented.
- **Session handling**: JWT token and user details are stored securely and checked on splash.
- **Main app UI**: Home, Plants, Calendar, Settings, Plant Details, Add Plant, and Edit Plant (placeholder) screens exist.
- **Plant CRUD**: Full CRUD (Create, Read, Update, Delete) is implemented and connected to the backend API.
- **Recent focus**: Completed a broad UI refresh for auth, main plant screens, settings, add/detail flows, launcher branding, password visibility, loading states, and plant list caching.

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
  - `data/`: Auth and Plant repositories, local token storage, network configuration, and API services.
  - `model/`: Shared plant and auth models.
  - `navigation/`: Route definitions, bottom-nav metadata, and `NavHost` setup.
  - `ui/components/`: Shared UI components for plant cards, bottom navigation, loading, and state handling.
  - `ui/screens/`: Auth, Home, Plants, Calendar, Settings, and secondary plant screens.

### 2. Navigation Flow
- Implemented app startup through `SplashScreen`.
- Splash checks for a JWT token before routing to Home.
- Login routes to Home on successful authentication.
- Register routes back to Login on successful account creation.
- Settings logout clears local session data before navigating to Login.
- Bottom navigation supports Home, Plants, Calendar, and Settings.
- Secondary navigation supports Plant Details, Add Plant, and Edit Plant screens.

### 3. Authentication & Session Management
- Added `AuthApiService` for register and login.
- Added `AuthRepository` for authentication workflows.
- Added `AuthViewModel` with `Idle`, `Loading`, `Success`, and `Error` auth states.
- Added `TokenManager` using encrypted shared preferences for:
  - JWT token
  - user email
  - display name
- Added `AuthInterceptor` to attach JWT bearer auth to authenticated requests.
- Login/register requests trim email input before submission.
- Backend error responses are parsed so UI can show messages such as `Invalid email or password.` instead of only HTTP codes.
- Logout clears encrypted preferences on `Dispatchers.IO`, then navigates after clearing completes.
- Login/Register password fields include show/hide visibility toggles.

### 4. Plant CRUD & Networking
- Implemented `PlantApiService` for full plant lifecycle management.
- Implemented `PlantRepository` with standardized `Result<T>` error handling.
- Implemented `PlantViewModel` to manage plant-related UI states (Idle, Loading, Success, Error).
- Connected `HomeScreen`, `PlantsScreen`, `PlantDetailsScreen`, and `AddPlantScreen` to real API data.
- Implemented Delete plant functionality with proper feedback and navigation.
- Added in-memory plant list caching so Home, Plants, and Calendar can reuse existing data instead of showing a full loading state on every tab switch.
- Plant API error bodies are parsed and surfaced in UI, including plain-text backend messages such as `Authenticated user missing.`.
- Aligned `CreatePlantRequest` and `UpdatePlantRequest` with .NET API DTOs.
- `UpdatePlantRequest` supports partial updates with nullable fields.
- Improved `RetrofitInstance` with safer lazy initialization and explicit initialization in `MainActivity`.

### 5. Main Screens & UI
- **Home**: Redesigned dashboard with hero summary, stats, care queue, recently added plants, and cleaner empty/error/loading states.
- **Plants**: Redesigned library with search, health filters, richer plant cards, and empty/error/loading states.
- **Plant Details**: Redesigned detail view with hero image, health overlay, care stat cards, notes card, edit/delete actions, and delete confirmation.
- **Add Plant**: Redesigned form with photo placeholder, styled fields, health-status selector, validation, loading, and error states.
- **Edit Plant**: Added navigation route and placeholder screen for plant updates.
- **Calendar**: Redesigned care schedule screen using plant data where available, with a weekly visual placeholder until real reminders are connected.
- **Settings**: Redesigned profile/settings screen with profile card, preference rows, account rows, and secure logout.
- **Health Status**: Standardized 0-2 integer mapping with improved color contrast for accessibility (Green, Gold, Red).
- **Launcher Icon**: Replaced the default Android launcher icon with a custom leaf/water adaptive icon matching the app brand.
- **Shared Plant UI**: Expanded `PlantComponents.kt` with shared colors, plant cards, stat tiles, section headers, status chips, and screen state components.

### 6. Infrastructure & Build
- Added Retrofit, Gson converter, OkHttp, and logging interceptor.
- Added timeouts in `RetrofitInstance`.
- Added Coil dependency for remote image loading.
- Added AndroidX Security Crypto for local sensitive data.
- Project builds successfully.

## Known Limitations
- Backend app endpoints now use JWT-only authentication.
- Watering calendar still needs connection to real reminder/history data.
- Edit Profile and Privacy Policy rows in Settings are placeholders.
- Dark Mode toggle is UI-only and does not yet change the app theme.
- Notifications toggle is UI-only and does not yet schedule reminders.
- Settings user info is read from local storage when the screen is composed; it is not reactive to profile edits yet.

## Next Steps

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
