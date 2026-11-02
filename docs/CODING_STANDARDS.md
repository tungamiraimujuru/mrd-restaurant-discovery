# Coding standards

Rules marked **enforced** are checked by `tools/check_standards.sh`, which runs in CI. The rest are
covered by Spotless (ktlint + Compose rules) and Android lint with warnings as errors, or are
conventions reviewed in pull requests.

## Architecture
- MVVM. Screens are stateless composables that take state and report events; ViewModels expose a
  `StateFlow` and plain functions for actions.
- Dependency injection with Hilt only, through constructors.
- Features depend on `:core:*` modules, never on each other. Only `:app` connects them. **Enforced**
- The data layer sits behind `RestaurantRepository` (in `:core:domain`). Room and Retrofit types do
  not leave their modules.

## Rules
1. All logging goes through `AppLogger`; `android.util.Log` is used only inside its implementation. **Enforced**
2. No non-null assertions (`!!`). **Enforced**
3. Flows are collected with `collectAsStateWithLifecycle()`. **Enforced**
4. Time comes from the injected `Clock`, never `System.currentTimeMillis()`. **Enforced**
5. Every activity extends `BaseActivity`, and edge-to-edge is enabled only through `displayEdgeToEdge()`. **Enforced**
6. Money is integer cents and is turned into text only by `CurrencyFormatter`. **Enforced**
7. No destructive database migrations: favorites are user data. **Enforced**
8. No `GlobalScope`. **Enforced**
9. Kotlin only; no Java files. **Enforced**
10. Repositories return `AppResult` instead of throwing; cancellation is always rethrown.

## Conventions
- A file holding one class is named after it.
- User-facing text lives in string resources (plurals for counts); the design system takes text as parameters.
- Formatting for display happens in mappers, not during composition.
- Composables take a `modifier` and have previews; a state with real logic gets a ViewModel test.
