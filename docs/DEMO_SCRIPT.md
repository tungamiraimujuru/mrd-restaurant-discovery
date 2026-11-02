# Demo script (about 8 minutes)

The brief asks for a voice-over covering the working app, the architecture, the key decisions and
the trade-offs. Record at the emulator's native size and keep the terminal font large.

## Before recording

- [ ] Emulator awake and unlocked, **debug** build installed, notifications silenced
- [ ] `adb shell pm clear com.mrd.discovery` so the first load is real
- [ ] Android Studio open on the project with the module tree expanded
- [ ] A terminal ready with the commands below
- [ ] Do a dry run once; the second run is the recording

## 1. Introduction (0:00 to 0:30)

"This is a restaurant discovery mini-app for Mr D. It is built with Kotlin and Compose, it is
offline-first, and it is split into modules the way a large app with several squads would be. I'll
show it working, walk the architecture, and finish with the trade-offs."

## 2. The app (0:30 to 3:30)

1. **Cold start.** Launch. Point out the loading state, then the list of 250 restaurants.
   "Everything on screen comes from a local database, never straight from the network."
2. **Scroll.** Show the card: image, open or closed, rating, ETA, fee. Point at a free-delivery card
   and one with no rating: "Missing data is handled. An unknown fee is hidden, a zero fee says Free."
3. **Search.** Type `sushi`, then `zzz` for the empty state, then clear it. "The text field updates
   instantly; only the database query is debounced."
4. **Open now.** Toggle it. Closed restaurants disappear.
5. **Details.** Open a restaurant, then back. Favorite it on the details screen, go back, and show the
   heart on the list agrees.
6. **Survives restart.** Run `adb shell am force-stop com.mrd.discovery`, relaunch, and the list and
   the favorite are there immediately.

## 3. The offline story (3:30 to 5:00)

1. Tap **DEV**, choose **Offline**, close the dialog, pull down on the list. The banner appears:
   "Working offline, showing saved restaurants." The list stays.
2. Choose **Timeout**, then **ServerError**, pull again. Different failure, same behaviour:
   "A failed refresh never damages what the user can see."
3. Choose **Normal**, pull again: the banner goes away.
4. The error screen:

   ```
   adb shell pm clear com.mrd.discovery
   adb shell am start -n com.mrd.discovery/.MainActivity --es backend_mode Offline
   ```

   "With nothing cached, there is nothing to show, so it is a full error with a retry." Open DEV,
   choose Normal, tap Retry, and the list loads.
5. The deep link:

   ```
   adb shell pm clear com.mrd.discovery
   adb shell am start -W -a android.intent.action.VIEW -d "mrd://restaurant/r-1001" com.mrd.discovery
   ```

   "Even with an empty cache, the details screen fills it itself." Then show an unknown id
   (`mrd://restaurant/nope`) landing on "Restaurant not found".

## 4. The architecture (5:00 to 7:00)

In Android Studio, in this order:

1. **Module tree.** "Ten modules. Features depend on core modules, never on each other. Only the app
   connects them." Show `MrDNavHost.kt`.
2. **Convention plugins** in `build-logic`: "A module's build file is a few lines."
3. **The repository** (`OfflineFirstRestaurantRepository`): "Reads come from Room, `refresh()` is
   the only code that calls the API, a failure keeps the cache."
4. **The mock server** (`FakeBackendInterceptor`): "The app makes real Retrofit calls. This
   interceptor answers them, with real exceptions and a real 503."
5. **`DiscoveryViewModel`**: "One state flow. The query is debounced, formatting runs once per result
   set off the main thread, query and filter live in saved state."

## 5. Quality and trade-offs (7:00 to 8:00)

1. Show the CI run: formatting, lint, the standards check, tests and build. Mention that the
   standards script fails the build on a rule breach.
2. "I chose to preserve state and cut per-keystroke work. Both are covered by tests."
3. Trade-offs, one sentence each: a mock backend, `LIKE` search that wants an FTS index or paging at
   scale, no HTTPS App Link without a domain, device tests not yet in CI.
4. Close: "SOLUTION.md has the scaling plan, the testing strategy and how I used AI in this project."

## If you get stuck

- Screen stuck on loading: DEV is on Slow latency. Switch it to Normal.
- No banner after Offline: you have to pull to refresh. The mode applies to the next refresh.
- The error screen does not appear: it needs a fresh install with the backend offline, as shown in step 3.4.
