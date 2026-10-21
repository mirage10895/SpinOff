# Architecture

SpinOff is a single-module (`:app`) Android application following an **MVVM** (Model–View–ViewModel) architecture.

## Layers

```
View (Activity / Fragment, ViewBinding)
   │  observes (LiveData)
   ▼
ViewModel  ──▶  Repository  ──▶  Remote API (Retrofit → TMDB)
   ▲                              │
   └──────────  Local database (Room: DAO + entities)
```

- **View**: `Activity` / `Fragment` using **ViewBinding**. Displays state and forwards user events; contains no business logic.
- **ViewModel**: exposes state to the View as `LiveData` and survives configuration changes. Never holds a reference to a View, `Context` or Activity.
- **Repository**: single entry point to data. Orchestrates the local (Room) and remote (TMDB) sources.
- **Model**: business objects (TMDB beans in `api/tmdb/beans`) and persistence entities (Room entities in `database/dao/model`).

## Source packages

| Package | Responsibility |
|---|---|
| `api/` | Network layer: Retrofit service, Gson adapters and TMDB response beans. |
| `database/` | Local persistence: Room `AppDatabase`, DAOs and entities. |
| `repositories/` | Repositories for movies and series (local + remote orchestration). |
| `repository/` | Library backup (JSON export/import). |
| `viewmodel/` | ViewModels and presentation logic (discovery strategies, statistics, backup state). |
| `ui/` | View layer: activities, fragments, adapters and shared UI helpers. |
| `services/` | `WorkManager` workers (background sync, migration). |
| `utils/` | Stateless helpers (dates, videos, countries, logging…). |

## Data flow

- **Read:** the View observes `LiveData` exposed by the ViewModel; the ViewModel delegates to a Repository, which reads either Room (library data) or the TMDB API (metadata).
- **Write:** the View triggers an action on the ViewModel → Repository → Room DAO. Writes run off the main thread.
- **Background:** `WorkManager` workers call repositories through their **synchronous** methods.

## Data storage

- **Room** is the source of truth for the user's library (movies, series, seasons, episodes, watched state).
- **TMDB** is the source of metadata; it is fetched on demand and cached into Room entities.
- **Backups** are plain JSON files produced/consumed by the backup repository via Gson.
