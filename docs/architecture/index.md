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
| `utils/` | Stateless helpers (dates, videos, countries, logging, notifications…). |

## Data flow

- **Read:** the View observes `LiveData` exposed by the ViewModel; the ViewModel delegates to a Repository, which reads either Room (library data) or the TMDB API (metadata).
- **Write:** the View triggers an action on the ViewModel → Repository → Room DAO. Writes run off the main thread.
- **Background:** `WorkManager` workers call repositories through their **synchronous** methods.

## Data storage

- **Room** is the source of truth for the user's library (movies, series, seasons, episodes, watched state).
- **TMDB** is the source of metadata; it is fetched on demand and cached into Room entities.
- **Backups** are plain JSON files produced/consumed by the backup repository via Gson.

### Table responsibilities

| Table | Owns |
|---|---|
| `movie` | Movies of the library and their watched flag. |
| `serie` | Series of the library, their metadata and their aggregated watched flag. |
| `serie_season` / `serie_episode` | Watched state: season flags and the rows of watched episodes. |
| `serie_notification` | Release tracking only: TMDB snapshot used for detection, notification throttle and the next announced episode (release calendar source). It never stores library or watched data, so it is intentionally **excluded from the JSON backup**. |

## Serie release notifications

The periodic `UpdateSeriesWorker` (WorkManager, daily) runs **two synchronisations with different
frequencies**, so the expensive one does not have to run every day:

| Synchronisation | Frequency | TMDB payload | Cost / serie |
|---|---|---|---|
| **Release check** (`SerieRepository.syncReleasesSync`) | daily | `GET /tv/{id}` without `append_to_response` | ~7 Ko |
| **Metadata refresh** (`SerieRepository.updateAllSeriesSync`) | when older than `METADATA_MAX_AGE` (10 days) | `GET /tv/{id}?append_to_response=credits,videos,recommendations` | ~37 Ko |

Both durations live in `UpdateSeriesWorker` (`WORK_INTERVAL`, `METADATA_MAX_AGE`) and the work
request is built from `WORK_INTERVAL`, so the schedule and the throttle cannot diverge.

The release check:

1. fetches the lightweight payload;
2. refreshes **only** the columns it owns through `SerieDAO.updateSerieReleaseSnapshot`
   (name, poster, season/episode counts, sync time) — genres, actors, directors and runtime are
   never touched, so a light payload cannot erase the full synchronisation's work. This rule is
   guarded by `SerieDAOQueryTest`;
3. compares the payload with the stored snapshot in `serie_notification`
   (`SerieReleaseDetector`, pure and unit tested);
4. persists the new snapshot **before** notifying, so a crash never replays a notification;
5. collects the releases and hands them to `NotificationUtils`.

Rules of the detector:

- unknown snapshot (first sync after the migration, or a newly added serie) → snapshot only, no
  notification, to avoid a burst of false positives;
- a new episode must be already aired and differ from the last one seen;
- a new season must be already aired and must not be a special (season `0`), because TMDB
  announces future seasons long before their release;
- an `Ended` / `Canceled` serie never notifies a release;
- at most one notification per serie and per synchronisation.

The announced episode (`nextEpisodeToAir`) is kept in `serie_notification` until it is aired plus
**7 days**, which is the retention window the future release calendar will read from.

`POST_NOTIFICATIONS` is requested only when the user enables the release toggle in the library
settings, so a denial stays reversible.
