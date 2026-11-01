# Git workflow

We use GitHub Flow: `main` is always releasable, all work happens on short-lived branches and lands through pull requests.

## Branches
- `feat/<scope>-<summary>`, `fix/...`, `chore/...`, `docs/...`, `test/...`, `perf/...`
- One branch per delivery phase or logical change.

## Commits
Conventional Commits: `type(scope): summary`, imperative, under 72 characters.
Types: feat, fix, perf, refactor, test, docs, build, ci, chore, style.
Each commit builds and passes `spotlessCheck` on its own.

## Pull requests
- Open against `main`, fill in the template.
- CI must be green: spotless, lint, unit tests, debug build.
- Merge with a merge commit so each step stays visible in history.
- Delete the branch after merging.

## Releases
Tag `main` with `vX.Y.Z`. The release workflow builds the APK and attaches it to a GitHub release.

## Local checks before pushing
    ./gradlew spotlessApply spotlessCheck lintDebug testDebugUnitTest assembleDebug
