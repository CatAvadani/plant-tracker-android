# Plant Tracker Android - Progress Summary

## Project Overview
A modern, calm plant care application built with Jetpack Compose and Material 3. The project is currently in **Phase 2: Networking & Auth**, focusing on connecting the app to the .NET Web API and implementing real user authentication.

## Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Navigation**: Navigation Compose
- **Icons**: Material Icons Extended
- **Networking**: Retrofit 2 & OkHttp 4
- **Image Loading**: Coil Compose
- **Security**: AndroidX Security Crypto
- **Architecture**: MVVM with Repository pattern.

## Completed Tasks

### 1. Architecture & Structure
- Created a clean, scalable folder structure:
  - `data/`: Local and network data handling.
  - `model/`: Shared data models.
  - `navigation/`: Routing definitions and NavGraph setup.
  - `ui/components/`: Reusable UI elements (Cards, Nav Bars, States).
  - `ui/screens/`: Feature-based screen organization (Auth, Home, Plants, Calendar, Settings).

### 2. Navigation Flow
- Implemented a complete navigation lifecycle:
  - **Splash Screen**: 2-second delay with app branding.
  - **Authentication**: Login and Register screens with form fields.
  - **Main App**: Home, Plants, Calendar, and Settings accessible via a Persistent Bottom Navigation Bar.
  - **Secondary Navigation**: Deep linking to Plant Details and Add Plant forms.

### 3. Features & Screens
- **Dashboard (Home)**: Summary cards for plant health and watering status, plus a vertical collection list.
- **Plant Library**: A dedicated list view of all tracked plants.
- **Plant Details**: Deep-dive view for individual plants showing care tips, location, frequency, and care actions.
- **Add Plant Form**: Static form UI with fields for name, species, frequency, and notes, including placeholder image upload.
- **Watering Calendar**: Schedule view with upcoming watering reminders.
- **Settings & Profile**: User profile card, notification/dark mode toggles, and logout functionality.

### 4. UI/UX & Components
- **Calm Green Theme**: Consistent use of soft greens (`#F1F8E9`, `#2E7D32`) and rounded Material 3 cards.
- **Reusable States**: `EmptyState` and `LoadingState` components for consistent feedback.
- **Input Handling**: Added basic form validation UI and specialized keyboard types for email/password.

### 5. Infrastructure & Networking Setup
- Added **Retrofit** and **OkHttp** for API integration.
- Implemented **AuthInterceptor** for JWT and API Key authentication.
- Created **RetrofitInstance** singleton with logging and timeout configurations.
- Implemented **TokenManager** using **EncryptedSharedPreferences** for secure storage of sensitive data.
- Created **AuthApiService** and **AuthModels** for login, register, and API key generation.
- Implemented **AuthRepository** and **AuthViewModel** to handle authentication logic.
- Connected **LoginScreen** and **RegisterScreen** to real API calls with state management (Idle, Loading, Success, Error).
- Added **Coil** for efficient asynchronous image loading from URLs.
- Added **Coroutines** for background task management.

## Project Structure
```text
app/src/main/java/com/catalina/planttracker/
├── data/
│   ├── local/
│   │   └── TokenManager.kt
│   ├── model/
│   │   └── AuthModels.kt
│   ├── network/
│   │   ├── ApiConfig.kt
│   │   ├── AuthApiService.kt
│   │   ├── AuthInterceptor.kt
│   │   └── RetrofitInstance.kt
│   └── repository/
│       └── AuthRepository.kt
├── model/
│   └── Plant.kt            # Data class and fakePlants list
├── navigation/
│   ├── Screen.kt           # Route definitions
│   └── PlantNavGraph.kt    # Navigation Host setup
├── ui/
│   ├── components/
│   │   ├── BottomNavigationBar.kt
│   │   ├── PlantComponents.kt
│   │   └── StateComponents.kt
│   └── screens/
│       ├── auth/
│       │   ├── SplashScreen.kt
│       │   ├── LoginScreen.kt
│       │   ├── RegisterScreen.kt
│       │   ├── AuthViewModel.kt
│       │   └── AuthViewModelFactory.kt
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
└── MainActivity.kt         # App Entry point
```

## Next Steps (Phase 3: Plants CRUD)
- Create **PlantApiService** and **PlantRepository**.
- Implement **PlantViewModel** to fetch and manage plants.
- Connect **PlantsScreen** and **PlantDetailsScreen** to real API data.
- Implement **AddPlantScreen** with real API submission.
- Add real **Image Picking** and upload capability (Phase 4).
