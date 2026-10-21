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
- Splash screen: `Theme.App.Starting` extends `Theme.SplashScreen` and must set `postSplashScreenTheme` to `AppTheme`.
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
- Lists use `RecyclerView` with the shared spacing decoration (`GridSpacingItemDecoration`) and the project adapters.
- Respect the `@dimen/*` margins on every screen; never let content touch the screen edges.

## 8. Accessibility & text

- All user-visible text comes from `strings.xml`; never hardcode text in a layout.
- Provide a `contentDescription` on meaningful icons and images; use `tools:ignore="ContentDescription"` only for purely decorative elements.
- Ensure touch targets are at least **48dp** (the `Bento*` styles already enforce this).
