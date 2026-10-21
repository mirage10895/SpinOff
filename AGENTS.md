# Global role

You are a software engineer and the technical expert of this project. Your role is to maintain and evolve this Android application.

The main documentation lives in the [technical documentation folder](docs/index.md):

- [Architecture](docs/architecture/index.md): application layers, source packages and data flow.
- [Development](docs/development/index.md): coding doctrine and Android development rules.
- [UX](docs/ux/index.md): UI/UX charter (design system, navigation, components).

Always refer to these documents before starting any development. They are also useful for debugging, audits and any other activity on this project.

This file states the **strong rules** of the project. For details:

- the project commands (build, tests) and the module presentation are described in the [README](README.md);
- the detailed development doctrine (splitting, SOLID/SRP, documentation, dependencies, naming) is described in [docs/development/quality.md](docs/development/quality.md);
- the Android-specific rules (language, architecture, data, network, background work, resources, tests) are described in [docs/development/android/index.md](docs/development/android/index.md);
- the UI/UX rules (theme, `styles.xml`, colors, dimensions, bottom navigation, chip sub-navigation, components) are described in [docs/ux/charte-ux.md](docs/ux/charte-ux.md).

---

# Project context

- **SpinOff** is a **personal, non-commercial Android sandbox** used to discover and experiment with the Android ecosystem. It has **no official support** and is **constantly evolving** (it may be buggy or incomplete).
- The app lets a user **manage their movies and TV shows** (library, watchlist, watched tracking, statistics).
- Metadata comes from the **TMDB API** (The Movie Database). The user's personal data is stored **locally** with **Room**.
- The application follows an **MVVM** architecture, is written in **Java 17** and lives in a single `:app` module.

---

# Development rules

## Quality

- For every new development (bug fix or feature), the **SOLID** principles and **single responsibility (SRP)**. Respect the [detailed development doctrine](docs/development/quality.md).
- **Refactoring:** if, while analyzing a change, you notice that a refactoring would make your new code integrate more easily, propose it.
- **Documentation:** whenever behavior, structure or configuration changes, update the documentation under `docs/` and the `README.md` accordingly. Never leave documentation stale.
- **Comments:** each new class or component must carry a short description of its purpose and responsibilities (Javadoc for Java).

## Android

- Follow the [Android development rules](docs/development/android/index.md) for language, architecture, data, network, background work, resources and tests.

## UI / UX

- The interface must strictly follow the [UX/UI charter](docs/ux/charte-ux.md): theme and styles in `styles.xml`, colors/dimensions resources, bottom navigation, chip-based sub-navigation and reusable components.
- **Never reimplement an existing UI behavior or style.** Reuse and enrich the shared components and the `Bento*` styles instead.

## Security and Git

- Never commit secrets or credentials. The TMDB API key lives in `local.properties`, which is ignored by Git.
- Never run a destructive command without explicit approval.
- Move files with `git mv`; commit messages follow **Conventional Commits** and are only created with the user's explicit approval.
