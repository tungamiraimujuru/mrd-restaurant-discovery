# Solution notes

## 1. Scope and what was built

The feature is a restaurant browse screen and a details screen on top of a networked, offline-first
data layer. Optional stretch goals (favorites and a deep link) are included because the storage and
navigation needed for them already existed. Out of scope: the other filter chips and navigation
elements in the designs, the menu, and the cart. The brief limits search and filters to a name
search and an "Open now" toggle, so nothing beyond that was built.

## 2. Modules and shared components

| Module | Responsibility |
|---|---|
| `:app` | Application, `MainActivity`, the navigation host, the manifest. Wiring only. |
| `:feature:discovery`, `:feature:details` | One screen each: state, ViewModel, composables, strings. |
| `:core:domain` | `Restaurant`, `RestaurantFilter`, `SyncState` and the `RestaurantRepository` contract. |
| `:core:data` | The repository implementation and the mappers that validate network records. |
| `:core:network` | Retrofit API, DTOs, and the in-app mock server. |
| `:core:database` | Room cache. Room types are internal; others see a small interface. |
| `:core:common` | Result and error types, the logger, the clock, dispatchers, money formatting. |
| `:core:designsystem` | Theme from the designs, and components that take their text as parameters. |
| `:core:ui` | Base activity, edge-to-edge, and presentation rules shared by features. |
| `build-logic` | Convention plugins (library, compose, hilt, feature). |

Rules, checked by `tools/check_standards.sh` in CI: features do not depend on features, and
`android.util.Log`, `System.currentTimeMillis()` and raw money formatting appear in exactly one
place each. Shared presentation rules (a missing fee is not the same as a free one) live in
`:core:ui` so two screens cannot disagree.

## 3. Navigation, state and data boundaries

**Navigation.** Routes are type-safe `@Serializable` classes owned by their feature. A feature
exposes one function that adds its destinations to a graph and reports events upward (a restaurant
was clicked, back was pressed). `:app` decides where an event leads, so features never import each
other. The deep link belongs to the details feature; the app only registers the scheme.

**State.** One-directional. A ViewModel combines the cached restaurants with the sync state and
turns them into an immutable UI state. The screen is stateless: it renders a state and reports
events. Search text and the filter live in `SavedStateHandle`, so they survive process death.

**Data.** The UI never talks to the network. Room is the single source of truth: reads come from it,
and `refresh()` is the only code that calls the API. The repository returns `AppResult` instead of
throwing, and it rethrows cancellation. Retrofit and Room types do not leave their modules.

## 4. The offline baseline

- A successful refresh replaces the cache in one transaction, so readers never see a half-written list.
- A failed refresh (no connection, timeout, 503) leaves the cache untouched and is reported through
  the sync state. The user sees their data with a banner saying how old it is, plus a retry.
- If nothing was ever cached, the user gets a full error screen with a retry instead.
- A feed whose records are all unusable cannot wipe a good cache; a genuinely empty feed can.
- Records without an id or name are dropped, and implausible values become "unknown" instead of
  being shown (a rating of 7.5, a negative fee).
- Favorites are in a separate table, so refreshing never touches them.

## 5. Testing

| Level | What it covers |
|---|---|
| Unit | Result mapping and cancellation, money formatting, search pattern escaping, the record mappers, the repository (fake network and database: success, failure keeping the cache, bad feed, in-flight and cancelled refresh), both ViewModels (every state, debounce, restoring saved state), the formatting rules |
| On-device (database) | The search, filter, ordering and favorite-retention queries against real SQLite |
| On-device (UI) | The discovery screen through every state and event, driven without a ViewModel |
| On-device (app) | Deep links including an unknown id, up navigation, restoration after recreation, scroll reset |
| Static | Spotless (ktlint and Compose rules), Android lint with warnings as errors, Compose lint rules, `check_standards.sh` |

CI runs formatting, lint, the standards check, unit tests and the debug build. Instrumented tests
need an emulator and are run locally; running them in CI on a managed device is the first thing to
add. With more time: screenshot tests for the design system, and contract tests between the app's
DTOs and the real backend schema.

## 6. Keeping the list smooth as it grows

Today (250 rows): stable item keys and a content type per item, immutable UI models, formatting done
once per result set off the main thread (not on every keystroke), the search query debounced, the
filter and sort done in SQL, and images loaded at the size they are drawn with a memory and disk cache.

If the list grew by orders of magnitude, in order of payoff:

1. **Paging.** Move to Paging 3 with a Room `PagingSource`, so only a window of rows is in memory.
   A `RemoteMediator` fills the database page by page from the API.
2. **Server-side search and filtering with cursors,** so the client never needs the whole catalogue
   and results can be personalised and location-aware.
3. **Full-text search.** `LIKE '%q%'` is a table scan. An FTS index keeps search instant at scale.
4. **Measure, don't guess.** Baseline Profiles for startup and scrolling, and Macrobenchmark frame
   timing in CI, so a regression is a failing build.
5. **Image costs.** Ask the server for thumbnails sized to the card instead of downsampling large files.

## 7. The measurable improvement

Chosen: preserve user input and scroll position, and reduce list work. Evidence:

- Instrumented tests recreate the activity and assert that search text, the filter and the scroll
  position survive. Query and filter are in `SavedStateHandle`, so they also survive process death.
- A new search starts at the top of the list. A test failed on the first attempt because
  `snapshotFlow` cannot see plain parameters; the fix reads them through `rememberUpdatedState`, and
  the test now also asserts the list really scrolled away before the search.
- Before, every keystroke and every sync state change re-formatted all restaurants on the main thread.
  Now it happens once per result set, off the main thread. Unit tests assert the list instance is reused.
- Scrolling does not touch that code, so no change in dropped frames is claimed. As a sanity check,
  `tools/measure_jank.sh` (20 round trips) on a release build on the `Pixel_10_Pro_XL` emulator gave
  500 frames with a median of 17 ms and 90th, 95th and 99th percentiles of 18, 19 and 19 ms
  (about one 60 Hz frame each). The percentage of "janky" frames varied between runs (28% and 42%),
  and an earlier run on the same emulator showed a 65 ms median, so emulator timings are only
  indicative. A Macrobenchmark on a physical mid-range device is the next step.

## 8. Scaling this for a large app with several squads

- **Ownership.** One owner per module via `CODEOWNERS`, with the design system and the data platform
  owned by dedicated teams. Convention plugins mean a new module is a four-line build file with the
  same lint, formatting and test setup as every other.
- **API and implementation modules.** For each feature, expose a small `api` module (route, entry
  point, public models) and hide the implementation, so a squad can change its internals without
  recompiling the modules that navigate to it. This repository already hides Room and Retrofit behind
  interfaces for the same reason.
- **Navigation.** Per-feature graphs behind a navigator interface owned by the app, so routes are
  contracts between squads and deep links stay with the feature that handles them.
- **Build speed.** Parallel builds, the configuration cache and the build cache; a dependency-graph
  check in CI (this repository already enforces "features never depend on features").
- **Shared UI.** A versioned design system with screenshot tests. Components take text as parameters
  so features own copy and localisation.
- **Safe delivery.** Trunk-based development with feature flags, so incomplete work merges without
  being visible, and the same gates for every change.

## 9. AI-assisted development

I used an AI assistant throughout, treating it as a fast pair programmer whose output is a draft.

**How it was kept productive**

- *Plan first, then small steps.* The work was broken into eleven phases and each phase into commits
  that build and pass on their own. Commands were chained so a failing check stops the commit.
- *Standards as input and as gates.* The architecture rules were given to the assistant up front, and
  then the build enforced them (formatting, lint with warnings as errors, a standards script), so
  generated code is held to the same bar as hand-written code.
- *Verify against reality.* Library versions were looked up on release pages, not taken from memory.
  Behaviour was checked on an emulator and in a release (R8) build, not assumed.

**What review and checks caught**

- A catalog with placeholder versions failed the build; an XML plural, a Compose `remember` misuse
  and a duplicated type were caught by lint and the compiler; a `snapshotFlow` bug was caught by a
  UI test; the newest emulator broke an old Espresso version, found only by running the tests.
- A feature branch cut from a stale base was caught by a compile error. The fix was a rebase, not a
  workaround.

**What I would keep, and what I would watch, on a team**

- Keep: shared standards and prompts in the repository under review, the same CI gates and a
  verification checklist in every pull request, and the assistant mostly for tests, boilerplate and
  migrations.
- Watch: security-sensitive code and dependency choices need a human decision, generated tests must
  be shown to fail for the right reason, and reviewers should review for understanding, not only
  for passing checks.

## 10. Trade-offs and what is next

- **Mock backend, not a service.** It behaves like a server (latency, errors, real HTTP types), but
  the contract with a real backend is untested. Next: contract tests.
- **No paging, `LIKE` search.** Right for hundreds of rows, wrong for hundreds of thousands (section 6).
- **No `https` App Link.** Verification needs a real domain; an unverified link only opens in the
  browser. Production would use Digital Asset Links.
- **Default launch mode.** A link into a running app opens a second activity; `singleTop` with a
  new-intent handler is the refinement.
- **Light theme, system font.** The designs define neither a dark scheme nor font files.
- **Instrumented tests not in CI,** and the release variant is debug-signed so it can be verified
  locally. Real signing would use secrets in CI, and a managed emulator job would run the device tests.
- **Favorites are local.** No account or sync.
