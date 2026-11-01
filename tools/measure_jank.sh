#!/usr/bin/env bash
# Counts dropped frames while scrolling the restaurant list up and down.
# Usage: tools/measure_jank.sh [round-trips]
# Have the app open on the list, on an emulator or device. Compare runs on the SAME device,
# since a debug build is slower than release: the numbers are relative, not absolute.
set -euo pipefail

PKG=com.mrd.discovery
ROUNDS=${1:-20}

read -r WIDTH HEIGHT < <(adb shell wm size | tail -1 | tr -d '\r' | sed -E 's/.*: ([0-9]+)x([0-9]+).*/\1 \2/')
X=$((WIDTH / 2))
TOP=$((HEIGHT / 4))
BOTTOM=$((HEIGHT * 3 / 4))

adb shell dumpsys gfxinfo "$PKG" reset > /dev/null
for _ in $(seq "$ROUNDS"); do
  adb shell input swipe "$X" "$BOTTOM" "$X" "$TOP" 250
  adb shell input swipe "$X" "$TOP" "$X" "$BOTTOM" 250
done

adb shell dumpsys gfxinfo "$PKG" | tr -d '\r' \
  | grep -E "Total frames rendered|Janky frames|50th percentile|90th percentile|95th percentile|99th percentile"
