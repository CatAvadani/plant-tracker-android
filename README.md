# Leaf Care

Leaf Care is a modern, calm, and intuitive plant care application built with Jetpack Compose and Material 3. It helps users manage their indoor and outdoor gardens by tracking plant health, watering schedules, and providing a centralized library for all their green companions.

The app is fully integrated with a .NET Web API for secure data persistence and user authentication.

## Features

- **Authentication**: Secure Login and Registration flow with JWT-based session management.
- **Dashboard (Home)**: Summary of garden health, quick statistics, and a "Needs Attention" queue for plants requiring immediate care.
- **Plant Library**: A comprehensive list of all tracked plants with search and status filtering.
- **Plant Details**: In-depth view for each plant including species, location, watering frequency, and notes.
- **Plant Management (CRUD)**: Full Create, Read, Update, and Delete operations for plants.
- **Watering Calendar**: A visual schedule for upcoming watering tasks.
- **Secure Storage**: Sensitive information like JWT tokens and API keys are stored using `EncryptedSharedPreferences`.
- **Responsive UI**: Built entirely with Jetpack Compose for a smooth and modern user experience.

## Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Navigation**: Navigation Compose (type-safe routing)
- **Architecture**: MVVM (Model-View-ViewModel) with Repository pattern
- **Networking**: Retrofit 2, OkHttp 4
- **Image Loading**: Coil Compose
- **Concurrency**: Kotlin Coroutines & Flow
- **Security**: AndroidX Security Crypto
- **Local Storage**: EncryptedSharedPreferences (Token Management)

## Project Structure

```text
app/src/main/java/com/catalina/planttracker/
├── data/           # Data layer (Repositories, API Services, Token Manager)
├── model/          # Domain & Network Models
├── navigation/     # Navigation Graph & Route definitions
├── ui/
│   ├── auth/       # Auth ViewModels
│   ├── plants/     # Plant ViewModels
│   ├── components/ # Reusable UI components
│   ├── screens/    # Feature-specific screens (Home, Auth, Plants, etc.)
│   └── theme/      # Material 3 Theme definitions
└── MainActivity.kt # Entry point
```

## Getting Started

### Prerequisites

- Android Studio Jellyfish or newer
- JDK 17
- Access to the Leaf Care .NET API (ensure the base URL is configured in `ApiConfig.kt`)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/LeafCareAndroid.git
   ```

2. **Open in Android Studio**:
   Select "Open" and navigate to the project root.

3. **Configure the API**:
   Open `app/src/main/java/com/catalina/planttracker/data/network/ApiConfig.kt` and set your backend base URL:
   ```kotlin
   object ApiConfig {
       const val BASE_URL = "https://your-api-url.com/"
   }
   ```

4. **Sync Gradle**:
   Wait for the project to sync and download dependencies.

5. **Run the app**:
   Select your emulator or physical device and click the "Run" button.

## Roadmap

- [ ] **Phase 4: Images & Reminders**: Real image picking, multipart upload support, and local notifications for watering.
- [ ] **Phase 5: Profile & Preferences**: Reactive profile edits, theme persistence, and notification settings.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
