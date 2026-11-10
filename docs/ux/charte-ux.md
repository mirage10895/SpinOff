---
description: UI/UX conventions of the project (theme, styles, colors, dimensions, navigation, components)
globs: app/src/main/res/**
alwaysApply: false
---

# UX/UI charter

The interface follows a dark, **Material Design** "Bento" style: rounded cards, low-elevation outlines, uppercase medium labels and filter chips.

**Never reimplement an existing UI behavior or style.** Reuse and enrich the shared styles and components defined in `res/values/styles.xml`.

## 1. Theme (`styles.xml`)

- Base theme: `AppTheme` extends `Theme.MaterialComponents.DayNight.NoActionBar`. It defines `colorPrimary`, `colorSecondary`, `colorAccent` and transparent status/navigation bars.
- Splash screen: `Theme.App.Starting` extends `Theme.SplashScreen`, uses the icon **foreground** (`@drawable/ic_launcher_foreground`) as its animated icon, and must set `postSplashScreenTheme` to `AppTheme`.
- Modal screens (detail activities opened from the bottom): use `AppTheme.NoActionBar.Modal` or `AppTheme.Modal`, which apply the `Animation.Activity.BottomUp` window animation (slide in/out from the bottom).
- Do not create a new base theme: extend `AppTheme` (or an existing derived style).

## 2. Colors (`res/values/colors.xml`)

| Token | Value | Usage |
|---|---|---|
| `colorPrimary` | `#4F9DA6` | Accent color, selected states, ripples |
| `colorSecondary` | `#121212` | Dark background |
| `colorAccent` | `#FF007A` | Highlight accent |
| `color_primary_semi_opaq` | `#4F9DA6E6` | Card/chip outlines |
| `card_background_color` | `#222222` | Cards, chips, bottom sheets |
| `white` | `#FFF` | Primary text/icons |
| `color_text_secondary` | `#99E5E2E1` | Secondary text, descriptions |
| `color_card_outline` | `#22FFFFFF` | Card outlines, off-state switch tracks |
| `color_primary_ripple` | `#334F9DA6` | Ripples on the dark bottom sheets |
| `search_clear_background` | `#3A3A3A` | Round background of the search field's clear button |

- **Never hardcode a hex color** in a layout or in code. Always reference a color resource. If a new color is needed, add it to `colors.xml` — and do not create near-duplicate shades for the same semantic intent.
- State lists (chip text/background, bottom navigation tint) live in `res/color/` (e.g. `chip_text_color.xml`, `bottom_nav_color.xml`).

## 3. Dimensions (`res/values/dimens.xml`)

| Token | Value |
|---|---|
| `activity_horizontal_margin` | `24dp` |
| `activity_vertical_margin` | `16dp` |
| `text_margin` | `10dp` |
| `grid_spacing` | `14dp` |

- **Never hardcode `dp` values** in layouts: use a `@dimen/...` resource. Add a new dimension to `dimens.xml` when a spacing is reused.

## 4. Shared components (styles)

Reuse these styles instead of redefining them:

- `BentoCard` (and `BentoCard.Outline`, `BentoCard.Outline.Primary`): rounded (24dp) flat cards with an optional outline.
- `MediaPosterCard`: poster tile of the discovery grids (12dp corners, accent outline toggled when the title is in the library).
- `BentoChip` (parent `Widget.MaterialComponents.Chip.Filter`): filter/selection chip.
- `BentoIconButton`: 48dp square outlined icon button.
- `BentoLabel` / `BentoValue` (`BentoValue.Large`): uppercase label and large value used in stat cards.
- `BentoSelector` / `BentoSelectorText`: outlined exposed-dropdown selector.
- `BentoBottomSheetDialog` / `BentoBottomSheetStyle` / `BentoBottomSheetShapeAppearance`: modal bottom sheet with rounded top corners.

## 5. Main navigation (bottom navigation)

- The root screen (`MainActivity`) uses a `BottomNavigationView` driven by `res/menu/menu_bottom_nav.xml`.
- The three top-level destinations are **Discover**, **Search** and **Library**. Adding a destination means adding an item to `menu_bottom_nav.xml` and a branch in the `setOnItemSelectedListener` of `MainActivity`.
- Keep `app:labelVisibilityMode="selected"` and both `app:itemIconTint` and `app:itemTextColor` set to `@color/bottom_nav_color`.
- Top-level destinations are switched by **replacing** a fragment in the `nav_host_fragment` container (no back-stack accumulation between tabs).

## 6. Sub-navigation (chips)

- Secondary navigation inside a screen (e.g. **Movies / Series** on the Discover screen, or **Popular / Top rated / Current**) uses a `ChipGroup` of `BentoChip`s — not tabs and not a second bottom bar.
- Configure the `ChipGroup` with `app:singleSelection="true"` and `app:selectionRequired="true"`, and hide the checked icon (`app:checkedIconVisible="false"`) for a segmented-control look.
- Give the group a clear id (`chip_group_type`, `chip_group_discovery_type`) and react to selection with `setOnCheckedStateChangeListener`.
- Status toggles (e.g. "In watchlist", "Watched") reuse the pattern in `layout_status_chips.xml` with `Widget.MaterialComponents.Chip.Filter` chips.

## 7. Layout conventions

- Screen titles use `TextAppearance.MaterialComponents.Headline4` in bold, with `@dimen/activity_horizontal_margin` horizontal padding and `@dimen/activity_vertical_margin` top padding.
- Content is laid out inside a `ConstraintLayout` or a vertical `LinearLayout` with `?android:attr/windowBackground` as background.
- Filters and settings open in a **modal bottom sheet** (`BentoBottomSheetDialog`) rather than a full screen.
- A settings sheet is a **sectioned list**: a `BentoLabel` section title, then full-width rows separated by a `@dimen/divider_thickness` divider. A **preference row** is clickable as a whole (title 16sp white + description 13sp `color_text_secondary`, control at the end, e.g. a `SwitchMaterial` tinted with `@color/switch_thumb_color` / `@color/switch_track_color`); an **action row** adds a leading icon, an optional secondary hint (`JSON`) and a trailing chevron. The touch target is the row, not only the control.
- Lists use `RecyclerView` with the shared spacing decoration (`GridSpacingItemDecoration`) and the project adapters.
- The search field is a `BentoCard` pill (no box stroke): a leading lens tinted `colorPrimary`, a borderless `AppCompatEditText`, and a trailing clear button drawn on a `search_clear_background` circle. The clear button keeps a **48dp touch target** (its visible circle is inset inside it) and only its visibility is toggled, so the text does not reflow while typing.
- Discovery poster grids (`DiscoverMediaAdapter`) flag titles already stored in the local library with a bookmark badge at the top-end corner and a `@color/colorPrimary` outline (`@dimen/discovery_in_library_stroke_width`). The badge carries a `contentDescription`; the outline is never the only signal. Reuse this pattern for any poster grid that mixes remote and local state.
- Respect the `@dimen/*` margins on every screen; never let content touch the screen edges.

## 8. Accessibility & text

- All user-visible text comes from `strings.xml`; never hardcode text in a layout.
- Provide a `contentDescription` on meaningful icons and images; use `tools:ignore="ContentDescription"` only for purely decorative elements.
- Ensure touch targets are at least **48dp** (the `Bento*` styles already enforce this).

## 9. Logo, launcher icon and media placeholders

### Launcher icon

- The app ships an **adaptive icon only** (`minSdk 26`): `res/mipmap-anydpi/ic_launcher.xml` and `ic_launcher_round.xml`. **Never add legacy `mipmap-*dpi` PNGs** — they would never be loaded.
- The adaptive icon is composed of:
  - background: `@color/ic_launcher_background` (`#233142`, dark navy);
  - foreground: `@drawable/ic_launcher_foreground` (vector "SPIN-OFF" ticket + popcorn);
  - monochrome: `@drawable/ic_launcher_monochrome` (silhouette used by Android 13+ themed icons).
- `ic_launcher_monochrome.xml` is derived from `ic_launcher_foreground.xml`: update both together when the artwork changes.
- `AndroidManifest.xml` references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.

### Splash screen

- `Theme.App.Starting` (parent `Theme.SplashScreen`) sets:
  - `windowSplashScreenAnimatedIcon` to `@drawable/ic_launcher_foreground` — **foreground only**, never the full adaptive icon, because the system applies its own mask and background;
  - `windowSplashScreenBackground` to `?android:attr/windowBackground`;
  - `postSplashScreenTheme` to `@style/AppTheme`.

### In-app illustration

- `@drawable/logo_no_back` (WebP, `drawable-nodpi`) is the only in-app version of the logo. It is used **solely** as the search screen's empty-state illustration (`layout/fragment_search_container.xml`), shared by the idle, "no results" and error states — only the caption text changes.
- It is decorative: keep a `contentDescription` consistent with the surrounding context and never use it as a fallback for a missing content image.

### Media placeholders

- `@drawable/ic_launcher_foreground` is the neutral placeholder for failed or missing remote posters, stills and watch-provider logos (Picasso `.error()` and the pre-bind default).
- `@drawable/ic_unknown` remains the avatar placeholder for artists.
- **Never use the launcher icon** (`ic_launcher_foreground` / `ic_launcher`) as a content placeholder.

### Adding or changing an asset

- Prefer a vector drawable. For bitmaps, ship them in `drawable-nodpi` as WebP unless density-specific scaling is intended.
- Update this section whenever the icon, the logo or the placeholder policy changes.

## 9. Logo, launcher icon and media placeholders

### Launcher icon

- The app ships an **adaptive icon only** (`minSdk 26`): `res/mipmap-anydpi/ic_launcher.xml` and `ic_launcher_round.xml`. **Never add legacy `mipmap-*dpi` PNGs** — they would never be loaded.
- The adaptive icon is composed of:
  - background: `@color/ic_launcher_background` (`#233142`, dark navy);
  - foreground: `@drawable/ic_launcher_foreground` (vector "SPIN-OFF" ticket + popcorn);
  - monochrome: `@drawable/ic_launcher_monochrome` (silhouette used by Android 13+ themed icons).
- `ic_launcher_monochrome.xml` is derived from `ic_launcher_foreground.xml`: update both together when the artwork changes.
- `AndroidManifest.xml` references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.

### Splash screen

- `Theme.App.Starting` (parent `Theme.SplashScreen`) sets:
  - `windowSplashScreenAnimatedIcon` to `@drawable/ic_launcher_foreground` — **foreground only**, never the full adaptive icon, because the system applies its own mask and background;
  - `windowSplashScreenBackground` to `?android:attr/windowBackground`;
  - `postSplashScreenTheme` to `@style/AppTheme`.

### In-app illustration

- `@drawable/logo_no_back` (WebP, `drawable-nodpi`) is the only in-app version of the logo. It is used **solely** as the search screen's empty-state illustration (`layout/fragment_search_container.xml`), shared by the idle, "no results" and error states — only the caption text changes.
- It is decorative: keep a `contentDescription` consistent with the surrounding context and never use it as a fallback for a missing content image.

### Media placeholders

- `@drawable/ic_launcher_foreground` is the neutral placeholder for failed or missing remote posters, stills and watch-provider logos (Picasso `.error()` and the pre-bind default).
- `@drawable/ic_unknown` remains the avatar placeholder for artists.
- **Never use the launcher icon** (`ic_launcher_foreground` / `ic_launcher`) as a content placeholder.

### Adding or changing an asset

- Prefer a vector drawable. For bitmaps, ship them in `drawable-nodpi` as WebP unless density-specific scaling is intended.
- Update this section whenever the icon, the logo or the placeholder policy changes.
