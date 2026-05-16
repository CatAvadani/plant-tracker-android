# Leaf Care

Leaf Care is a modern, calm, and intuitive plant care application built with Jetpack Compose and Material 3. It helps users manage their indoor and outdoor gardens by tracking plant health, watering schedules, and providing a centralized library for all their green companions.

The app is fully integrated with a .NET Web API for secure data persistence and user authentication.

## Features

- **Authentication**: Secure Login and Registration flow with JWT-based session management.
- **Dashboard (Home)**: Summary of garden health, quick statistics, and a "Needs Attention" queue for plants requiring immediate care.
- **Plant Library**: A comprehensive list of all tracked plants with search and status filtering.
- **Plant Details**: In-depth view for each plant including species, location, watering frequency, notes, and care history.
- **Plant Management (CRUD)**: Full Create, Read, Update, and Delete operations for plants.
- **Image Upload**: Pick and upload plant photos via multipart requests.
- **Care Logs**: Track watering, fertilizing, pruning, and other care activities per plant.
- **Watering Calendar**: A visual schedule for upcoming watering tasks.
- **Watering Reminders**: Local notifications powered by `AlarmManager` to remind users when plants need water. Reminders are automatically restored after device reboot.
- **Notification Preferences**: Toggle reminders on or off.
- **Secure Storage**: JWT tokens and user session details are stored using `EncryptedSharedPreferences`.
- **Responsive UI**: Built entirely with Jetpack Compose for a smooth and modern user experience.

## Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Navigation**: Navigation Compose
- **Architecture**: MVVM (Model-View-ViewModel) with Repository pattern
- **Networking**: Retrofit 2, OkHttp 4
- **Image Loading**: Coil Compose
- **Concurrency**: Kotlin Coroutines & Flow
- **Security**: AndroidX Security Crypto (`EncryptedSharedPreferences`)
- **Notifications**: AlarmManager, BroadcastReceiver, NotificationManagerCompat
- **Local Storage**: EncryptedSharedPreferences (Token Management), plain SharedPreferences (Notification settings)

## Project Structure

```text
app/src/main/java/com/catalina/planttracker/
├── data/                    # Data layer (Repositories, API Services, Models)
│   ├── auth/                # Auth repository
│   ├── carelogs/            # Care log repository
│   ├── local/               # Token Manager (EncryptedSharedPreferences)
│   ├── model/               # DTOs for API payloads
│   ├── network/             # Retrofit setup, API services, AuthInterceptor
│   ├── notifications/       # Notification preference manager
│   └── plants/              # Plant repository
├── model/                   # Domain models
├── navigation/              # Navigation Graph & Route definitions
├── notifications/           # Alarm scheduling, reminder receiver, boot receiver
└── ui/
    ├── auth/                # Auth ViewModels
    ├── carelogs/            # Care log ViewModel
    ├── plants/              # Plant ViewModels
    ├── components/          # Reusable UI components
    ├── screens/             # Feature-specific screens (Home, Auth, Plants, Settings, etc.)
    └── theme/               # Material 3 Theme definitions
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

- [x] **Phase 3: Plants CRUD** — Complete plant management wired to the live API.
- [~] **Phase 4: Images & Reminders** — Real image picking, multipart upload support, and local notifications for watering (partially complete; calendar connection to real reminders in progress).
- [ ] **Phase 5: Profile & Preferences** — Reactive profile edits, theme persistence, token refresh, and full notification settings.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
