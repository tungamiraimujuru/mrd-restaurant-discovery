#!/usr/bin/env bash
# Enforces the rules in docs/CODING_STANDARDS.md that the compiler and lint cannot.
# Comment lines are ignored. Tests, build output and tools are not scanned.
set -uo pipefail
cd "$(dirname "$0")/.."

failures=0

scan() { # scan <rule> <regex> [allowed-file-regex]
  local rule="$1" regex="$2" allowed="${3:-^$}"
  local hits
  hits=$(grep -rnE "$regex" --include='*.kt' --include='*.kts' . \
      --exclude-dir=build --exclude-dir=.gradle --exclude-dir=.git --exclude-dir=.claude \
      --exclude-dir=test --exclude-dir=androidTest --exclude-dir=tools 2>/dev/null \
    | grep -vE '^[^:]+:[0-9]+:[[:space:]]*(//|\*|/\*)' \
    | grep -vE "$allowed" || true)
  if [ -n "$hits" ]; then
    echo "FAIL: $rule"
    echo "$hits" | sed 's/^/      /'
    failures=$((failures + 1))
  else
    echo "ok:   $rule"
  fi
}

scan "logging goes through AppLogger" 'import android\.util\.Log|android\.util\.Log\.' '/AndroidAppLogger\.kt:'
scan "no non-null assertions (!!)" '!!'
scan "flows use collectAsStateWithLifecycle" 'collectAsState\('
scan "time comes from the injected Clock" 'System\.currentTimeMillis|SystemClock\.(uptimeMillis|elapsedRealtime)|Instant\.now\(' '/DeviceClock\.kt:'
scan "activities extend BaseActivity" ':[[:space:]]*(ComponentActivity|AppCompatActivity|FragmentActivity)\(\)' '/BaseActivity\.kt:'
scan "edge-to-edge goes through displayEdgeToEdge" 'enableEdgeToEdge\(' '/EdgeToEdge\.kt:'
scan "money text comes from CurrencyFormatter" 'toPlainString|toEngineeringString' '/CurrencyFormatter\.kt:'
scan "no destructive database migrations" 'fallbackToDestructiveMigration'
scan "no GlobalScope" 'GlobalScope'
scan "no leftover placeholders" 'TEMPORARY|TODO|FIXME'
scan "features do not depend on other features" 'projects\.feature\.' '^\./app/'

java_files=$(find . -name '*.java' -not -path '*/build/*' -not -path './.git/*' -not -path './.gradle/*')
if [ -n "$java_files" ]; then
  echo "FAIL: Kotlin only"
  echo "$java_files" | sed 's/^/      /'
  failures=$((failures + 1))
else
  echo "ok:   Kotlin only"
fi

if [ "$failures" -gt 0 ]; then
  echo
  echo "$failures rule(s) broken. See docs/CODING_STANDARDS.md."
  exit 1
fi
echo
echo "All coding standards checks passed."
