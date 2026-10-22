# Detailed development doctrine

This document details the development doctrine of the project, referenced by `AGENTS.md`. It applies to all source code.

## 1. Code splitting & SOLID

- **File size:** no source file should exceed **200 lines**. If a file approaches or exceeds this limit, extract dedicated sub-components, helper classes or orchestration services, each with a single responsibility.
- **Method size:** methods must be short (ideally fewer than **25 lines**) and focus on a single task.
- **SOLID & Single Responsibility:** every class or component must have only one reason to change.
- **No duplication:** factor out shared logic into `utils/` or dedicated components instead of copy-pasting.

## 2. Documentation

- Add a short comment on every class or component you create to recall its purpose and responsibilities (Javadoc for Java).
- Keep comments consistent when you update the code.
- Update the documentation under `docs/` and the `README.md` when behavior, structure or configuration changes.

## 3. Dependency choice (library vs custom)

For any notable functional need, document a **"library vs custom"** decision: list the candidates (internal reuse, mature external libraries), compare them briefly (maturity, weight, scope, maintenance) and retain one option — project reuse / add a library / custom implementation — following the "Chosen option / Rejected alternative" pattern with its justification. For a trivial need reusing existing code, a single sentence ("no library needed") is enough.

- Only use dependencies already declared in [`app/build.gradle`](../../app/build.gradle) unless the choice above explicitly justifies adding one.

## 4. Naming

- **Java classes:** `PascalCase`, one public class per file, the file name equals the class name.
- **Packages:** lowercase, no underscores.
- **Resources:** `snake_case` (e.g. `fragment_home.xml`, `menu_bottom_nav.xml`).
- **Layout ids:** `snake_case`, prefixed by type (`btn_`, `chip_`, `text_`, `recycler_`).

## 5. Documented decisions

### Serie release notifications — library vs custom

- **Need:** notify new episodes/seasons of the series in the library, and keep their next announced release for the future calendar.
- **Candidates:** `NotificationCompat` + `NotificationChannel` (AndroidX, already pulled in by `androidx.core`); an external notification library; a custom `Notification.Builder` implementation.
- **Chosen option:** AndroidX `NotificationCompat` / `NotificationManagerCompat`. It is already a transitive dependency, works from API 26 (project minSdk), and handles the API 33 permission gracefully.
- **Rejected alternatives:** an external notification library (adds weight for a need fully covered by AndroidX); a raw `Notification.Builder` (API-level branching and no backport).
- **Scheduling:** reuse the existing `WorkManager` periodic `UpdateSeriesWorker`; no extra dependency.
- **Rejected data source:** TMDB `/tv/changes` (list of edited series IDs). It is an editorial edit log, not a broadcast feed: it requires 26+ requests per day for the ID list alone and floods false positives when metadata is retouched. The canonical `lastEpisodeToAir` / `numberOfSeasons` already returned by `GET /tv/{id}` is both cheaper and more accurate.
