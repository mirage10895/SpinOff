---
description: Android development rules for this project (language, architecture, data, network, background work, resources, tests)
globs: app/src/main/**
alwaysApply: false
---

# Android development rules

## 1. Language & platform

- The application is written in **Java 17**. Do not introduce Kotlin without an explicit "library vs custom" decision (see the [development doctrine](../quality.md)).
- Use **AndroidX only**; never use the legacy `android.support.*` libraries.
- Respect the configured SDK levels (`minSdk 26`, `compileSdk`/`targetSdk 37`) and Java compatibility (`JavaVersion.VERSION_17`).
- Prefer the modern Java APIs available since API 26 (`java.time`, `Optional`, streams) over legacy equivalents.
- Use the `Optional` API instead of returning or handling `null` where it makes sense.
- Do **not** use wildcard imports (e.g. `import java.util.*`).
- New files must contain a short description of their scope/responsibility.

## 2. Architecture (MVVM)

- Respect the layering described in the [architecture doc](../../architecture/index.md): View → ViewModel → Repository → (Room / TMDB API).
- **Views** (`Activity` / `Fragment`) must not contain business logic, direct API calls or direct database access.
- **ViewModels** expose state as `LiveData` (AndroidX `lifecycle-viewmodel` + `lifecycle-livedata`) and must never hold a `Context`, `View` or `Activity` reference.
- **Repositories** are the only classes allowed to talk to the network and the database. They expose `LiveData` for reads and run writes off the main thread.
- Use the **Strategy** pattern for variants of the same behavior (see `viewmodel/discovery/strategy`).
- Prefer explicit collaborator fields (constructor/parameter injection) so classes stay testable.

## 3. UI

- Use **ViewBinding** (`buildFeatures.viewBinding = true`); never use `findViewById`.
- Inflate the layout in `onCreateView` / `onCreate`, keep a binding field, and clear it in `onDestroyView` to avoid leaks.
- Use `RecyclerView` with dedicated adapters for lists; never build lists by looping over `LinearLayout` children.
- Any user-visible text must come from `strings.xml` (`@string/...`); never hardcode user-facing strings in code or layouts.
- Respect the [UX/UI charter](../../ux/charte-ux.md) for theme, styles, navigation and components.

## 4. Data (Room)

- The Room database is the **single source of truth** for the user's library.
- Declare entities in `database/dao/model`, DAOs in `database/dao` and the database in `database/DBInitializer/AppDatabase`.
- DAOs return `LiveData` for observable reads and plain values for one-shot / synchronous reads used in workers.
- Never touch the database on the main thread: use the repository's executor or a `WorkManager` worker.
- When the schema changes, bump the database version and provide a migration; the exported schema lives in `app/schemas`.
- Use the provided `RoomTypeConverter` for non-primitive types (e.g. dates).

## 5. Network (Retrofit)

- All remote calls go through the `api/` package, using **Retrofit 3** with the **Gson** converter and the **OkHttp** logging interceptor.
- The Retrofit interface (`TmdbApi`) declares the endpoints; the API repository (`TmdbApiRepository`) builds the Retrofit instance and handles calls and errors.
- Never call the API from a View or a ViewModel directly.
- Never log or commit the API key: it is injected at build time into `BuildConfig.TMDB_API_KEY`.
- Model API responses with dedicated beans under `api/tmdb/beans`; never leak raw `JsonObject`s to the upper layers.

## 6. Background work

- Use **WorkManager** (`services/`) for deferrable, guaranteed background work (periodic sync, migrations).
- Workers run on a background thread: use the repositories' **synchronous** methods (`...Sync`) there.
- Declare the required constraints (network, battery) when scheduling work.

## 7. Resources

- Colors, dimensions and strings live in `res/values` (`colors.xml`, `dimens.xml`, `strings.xml`) and must be referenced by name — never hardcode `#RRGGBB`, `dp` values or text in layouts.
- Reuse the shared `Bento*` styles and component styles instead of redefining them (see the [UX charter](../../ux/charte-ux.md)).
- Load images with **Picasso**; never implement network image loading manually.

## 8. Logging

- Use the project logging helper (`utils/LogUtils`) rather than scattering raw `android.util.Log` calls. Note that it only emits logs in debug builds.
- Never log sensitive data (API key, personal library content).
