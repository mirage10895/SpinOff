# SpinOff

![Status](https://img.shields.io/badge/status-WIP%20%C2%B7%20personal%20project-orange)
![Android](https://img.shields.io/badge/Android-8.0%20(API%2026)%2B-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/architecture-MVVM-blue)
![API](https://img.shields.io/badge/data-TMDB-01B4E4?logo=themoviedatabase&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-green)

> An Android app to manage your **movies** and **TV shows**, powered by data from **The Movie Database (TMDB)**.

## ⚠️ Disclaimer

This is a **personal project — not commercialized, with no official support**.

- It started as an **Android sandbox**: its main purpose is to help me **discover and experiment with the Android ecosystem** (architecture, local database, network calls, background work, UI…).
- It is **constantly evolving**: features, architecture and code change regularly, sometimes in breaking ways.
- As a result, it may be **buggy**, unstable or incomplete. No guarantee of functionality, fixes or compatibility is provided.
- **No support** is provided. Issues and pull requests may go unaddressed.

Use it at your own risk, ideally for learning purposes. 🙂

## 📖 Table of Contents

- [About](#-about)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Requirements](#-requirements)
- [Configuration](#-configuration)
- [Build & Run](#-build--run)
- [Project Structure](#-project-structure)
- [License](#-license)

## 🎬 About

**SpinOff** lets you browse, search and organize your movie and TV show consumption. Users build their own library (to watch / watched), track their series progress (seasons and episodes), view detailed pages (overview, cast, trailers, streaming providers) and access personal statistics about what they have watched.

All metadata comes from the **TMDB** API (titles, posters, IMDb ratings, genres, release dates, casts, streaming providers…). The user's personal data (library, tracking) is stored **locally** in a **Room** database.

## ✨ Features

- **Discovery**: movie and TV show lists (Popular, Top Rated, Now Playing / Currently Airing).
- **Advanced filters**: genre, year, origin country, runtime (runtime range chips tailored to movies vs. series).
- **Search**: search for movies, TV shows and people (actors, directors).
- **Detailed pages**:
  - Movies: overview, rating, budget, runtime, languages, countries, genres, cast, recommendations.
  - TV shows: seasons, episodes, networks, cast, recommendations.
  - People: biography, birthday, filmography.
- **Trailers**: watch teasers with an embedded YouTube player.
- **Where to watch**: available streaming providers (via TMDB) and direct launch in **Stremio** through a deep link.
- **My Library**:
  - Add / remove movies and TV shows.
  - Watchlist and "watched / not watched" tracking.
  - Season-by-season and episode-by-episode tracking.
- **Personal statistics** (movies and TV shows): total watch time, number of movies / seasons / episodes watched, preferred genres, distribution by years and decades, average/median ratings, top actors and directors, original languages, Hollywood vs. international…
- **Backup**: export / import your library as **JSON**.
- **Background sync**: periodic series updates via **WorkManager**.
- **Theme**: Material Design UI with an animated splash screen.

## 🏗️ Architecture

The project follows an **MVVM** (Model–View–ViewModel) architecture organized in layers:

```
View (Activity / Fragment, ViewBinding)
   │  observes (LiveData)
   ▼
ViewModel  ──▶  Repository  ──▶  Remote API (Retrofit → TMDB)
   ▲                              │
   └──────────  Local database (Room: DAO + Entities)
```

- **View**: `Activity` / `Fragment` using **ViewBinding**, purely dedicated to display and interactions. It observes the `LiveData` exposed by the ViewModel.
- **ViewModel**: exposes state to the View as `LiveData`, survives configuration changes and never holds a direct reference to the View.
- **Repository**: single entry point to data (`MovieRepository`, `SerieRepository`, `BackupRepository`, `TmdbApiRepository`). It orchestrates remote (TMDB) and local (Room) sources.
- **Model**: business objects and database entities (TMDB beans in `api/tmdb/beans`, Room entities in `database/dao/model`).

A few complementary patterns structure the code:

- **Strategy** for discovery (`DiscoveryStrategy` → `MovieDiscoveryStrategy` / `SerieDiscoveryStrategy`).
- Room **DAO** for local database access (`MovieDAO`, `SerieDAO`, `SeasonDAO`, `EpisodeDAO`).
- **Worker** (WorkManager) for background tasks (`UpdateSeriesWorker`, `DatabaseMigrationWorker`).

## 🧰 Tech Stack

| Area | Technology |
|---|---|
| Language | **Java 17** |
| Platform | **Android SDK** (compileSdk / targetSdk 37, minSdk 26) |
| Build | **Gradle** + **Android Gradle Plugin 9.1.1** |
| UI | **AndroidX AppCompat**, **Material Components**, **ConstraintLayout**, **RecyclerView**, **CardView**, **GridLayout**, **SwipeRefreshLayout**, **ViewBinding** |
| Splash screen | **AndroidX Core SplashScreen** |
| Architecture | **MVVM** — `ViewModel` + `LiveData` (AndroidX Lifecycle) |
| Networking | **Retrofit 3** + **converter-gson** + **OkHttp Logging Interceptor** |
| Local database | **Room 2.8** (runtime + annotation processor) |
| Background tasks | **WorkManager** |
| Image loading | **Picasso** |
| Video | **Android YouTube Player** (Pierfrancesco Soffritti) |
| Productivity | **Lombok** |
| Testing | **JUnit 4**, **AndroidX Test (JUnit)** and **Espresso** |

## ✅ Requirements

- A recent **Android Studio** (with support for AGP 9.1.1).
- **JDK 17**.
- The **Android SDK** with the platform matching `compileSdk 37`.
- A **device or emulator running Android 8.0 (API 26) or newer**.
- A (free) **TMDB API key**: [https://www.themoviedb.org/settings/api](https://www.themoviedb.org/settings/api).

## 🔧 Configuration

The app reads the TMDB API key from the `local.properties` file (not versioned, ignored by Git). Add:

```properties
tmdb.api.key=YOUR_TMDB_API_KEY
```

This key is injected at build time into `BuildConfig.TMDB_API_KEY` (see `app/build.gradle`).

> ℹ️ The `local.properties` file must also contain the Android SDK path (`sdk.dir=…`), usually filled in automatically by Android Studio.

## 🚀 Build & Run

From the command line:

```bash
# Build the debug variant
./gradlew assembleDebug

# Install on the connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Clean the project
./gradlew clean
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

**Debug** builds use the `.debug` application suffix and the `spinoffapp` version suffix.

## 📂 Project Structure

```
app/src/main/java/fr/eseo/dis/amiaudluc/
├── api/                 # Network layer (Retrofit, Gson, TMDB beans)
│   ├── tmdb/            #   TMDB API, response beans, API repository
│   └── gson/            #   Gson adapters (LocalDate, Instant, interfaces)
├── database/            # Local persistence (Room)
│   ├── dao/             #   DAOs (movies, series, seasons, episodes)
│   └── DBInitializer/   #   AppDatabase, type converters
├── repositories/        # Repositories (movies, series)
├── repository/          # BackupRepository (JSON export / import)
├── viewmodel/           # ViewModels (MVVM) and presentation logic
│   ├── discovery/       #   Discovery: strategies, filters, types
│   ├── movie/ · serie/  #   ViewModels + statistics
│   └── backup/          #   Backup state
├── ui/                  # View layer (Activities, Fragments, Adapters)
│   ├── movies/ · series/ · season/ · episode/
│   ├── library/         #   My Library
│   ├── artists/ · search/
│   └── common/          #   Reusable components (adapters, charts, filters)
├── services/            # WorkManager workers (sync, migration)
└── utils/               # Utilities (dates, videos, notifications, countries…)
```

The code lives in a single module, `:app` (see `settings.gradle`).

## 📄 License

Released under the **MIT** license — see the [LICENSE](LICENSE) file. The software is provided "as is", without warranty of any kind.

## 🙏 Credits

- Data and images provided by **[The Movie Database (TMDB)](https://www.themoviedb.org/)**. This product uses the TMDB API but **is not endorsed or certified by TMDB**.
- Personal project by [@mirage10895](https://github.com/mirage10895).
