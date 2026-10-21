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
