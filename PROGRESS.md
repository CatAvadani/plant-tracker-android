# Plant Tracker Android - Progress Summary

## Project Overview
A modern, calm plant care application built with Jetpack Compose and Material 3. The project is currently in **Phase 1: UI Foundation**, focusing on creating a solid, static user interface and navigation structure before adding backend functionality.

## Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Navigation**: Navigation Compose
- **Icons**: Material Icons Extended
- **Networking**: Retrofit 2 & OkHttp 4
- **Image Loading**: Coil Compose
- **Security**: AndroidX Security Crypto
- **Architecture**: Beginner-friendly folder-based separation of concerns.

## Completed Tasks

### 1. Architecture & Structure
- Created a clean, scalable folder structure:
  - `model/`: Data models and mock data.
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

### 5. Infrastructure Setup
- Added **Retrofit** and **OkHttp** for future API integration.
- Implemented **AuthInterceptor** for JWT and API Key authentication.
- Created **RetrofitInstance** singleton with logging and timeout configurations.
- Implemented **TokenManager** using **EncryptedSharedPreferences** for secure storage of sensitive data.
- Added **Coil** for efficient asynchronous image loading from URLs.
- Added **Coroutines** for background task management.

## Project Structure
```text
app/src/main/java/com/catalina/planttracker/
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
└── MainActivity.kt         # App Entry point
```

## Next Steps (Phase 2)
- Introduce **ViewModels** for state management.
- Implement **Local Database (Room)** for persistent plant storage.
- Add real **Image Picking** capability.
- Implement a real **Calendar Component**.
