# DEXAnalyze

An Android application for analyzing and tracking DEXA (Dual-Energy X-Ray Absorptiometry) scan results. Visualize your body composition data with comprehensive charts, trends, and health insights.

## Features

- **OAuth 2.0 Authentication**: Secure login via Keycloak
- **DEXA Scan Analysis**: View body composition metrics (fat %, muscle mass, bone density, etc.)
- **Trend Tracking**: Monitor changes in body composition over time
- **Interactive Charts**: Visualize data with charts and metrics
- **Demo Mode**: Explore the app with sample data without authentication
- **Unit Preferences**: Toggle between metric and imperial units
- **Dark Theme Support**: Native Material Design dark mode

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Networking**: Retrofit + OkHttp
- **Data Storage**: DataStore (preferences), in-memory repository
- **Auth**: OAuth 2.0 PKCE flow via Keycloak
- **Charts**: Custom Compose-based visualizations
- **Build System**: Gradle 8.x with version catalogs

## Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/aarw/dexanalyze/
│   │   ├── DEXAnalyzeApp.kt              # App entry point
│   │   ├── MainActivity.kt               # Activity host
│   │   ├── data/
│   │   │   ├── api/                      # API client & service
│   │   │   ├── auth/                     # OAuth & token management
│   │   │   ├── model/                    # Data models
│   │   │   ├── preferences/              # User settings
│   │   │   └── repository/               # Data layer
│   │   └── ui/
│   │       ├── screens/                  # Navigation destinations
│   │       ├── components/               # Reusable UI components
│   │       ├── theme/                    # Material Design theme
│   │       ├── navigation/               # Navigation graph
│   │       ├── LocalUnits.kt             # Unit preferences
│   │       └── Tooltips.kt               # Info tooltips
│   └── res/
│       ├── drawable/                     # App icons & assets
│       ├── values/                       # Strings, colors, themes
│       └── xml/                          # Network security config
└── build.gradle.kts                      # App module config
```

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11+
- Android SDK 28+ (targets API 34)

### Build & Run

```bash
# Clone and open in Android Studio
git clone <repo-url>
cd DEXAnalyze

# Build and run
./gradlew assembleDebug
# Or use Android Studio's Run button (Shift+F10)
```

### Configuration

OAuth endpoints are configured in `OAuthConfig.kt`:
- **Auth Server**: `https://auth.bodyspec.com`
- **API Server**: `https://app.bodyspec.com/api/v1`
- **Client ID**: `bodyspec-api-ext-v1` (configured for PKCE flow)

To use a different auth server, update the constants in `OAuthConfig.kt` and `AuthRepository.kt`.

## Security

- OAuth 2.0 with PKCE (Proof Key for Code Exchange) — secure for mobile apps
- Tokens stored in Android `EncryptedSharedPreferences` (via TokenStore)
- No sensitive data logged (tokens, response bodies)
- Network security config enforces HTTPS

## Demo Mode

Launch the app without authentication to explore with sample data. Toggle demo mode in Settings.

## Development

### Key Classes

- `AuthRepository`: OAuth login/token refresh flow
- `ScanRepository`: Fetch and cache DEXA scans (demo/live)
- `DashboardViewModel`: Main screen state management
- `AnalysisViewModel`: Detailed scan analysis
- `BodySpecApiService`: Retrofit API interface

### Navigation

Navigation graph defined in `AppNavigation.kt`:
- **Login** → Dashboard (after auth)
- **Dashboard** → Analysis, Progress, Settings

## Testing

Example unit test provided in `ExampleUnitTest.kt`.  
Example instrumented test provided in `ExampleInstrumentedTest.kt`.

Run tests:
```bash
./gradlew test                 # Unit tests
./gradlew connectedAndroidTest # Instrumented tests
```

## License

Proprietary — BodySpec Inc.

## Contact

For questions or feedback, reach out to the DEXAnalyze team.
