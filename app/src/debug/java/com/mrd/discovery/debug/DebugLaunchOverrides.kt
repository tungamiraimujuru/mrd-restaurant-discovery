package com.mrd.discovery.debug

import android.content.Intent
import com.mrd.discovery.core.network.fake.FakeBackendConfig

private const val EXTRA_MODE = "backend_mode"
private const val EXTRA_LATENCY = "backend_latency_ms"

/**
 * Lets adb start the app in a chosen backend state, for example to show the error screen on a
 * fresh install:
 *   adb shell am start -n com.mrd.discovery/.MainActivity --es backend_mode Offline
 * Called from onCreate before anything is composed, so the first refresh already sees it.
 */
fun applyDebugLaunchOverrides(intent: Intent, fakeBackend: FakeBackendConfig) {
    intent.getStringExtra(EXTRA_MODE)?.let { raw ->
        FakeBackendConfig.Mode.entries
            .firstOrNull { it.name.equals(raw, ignoreCase = true) }
            ?.let { fakeBackend.mode = it }
    }
    if (intent.hasExtra(EXTRA_LATENCY)) {
        fakeBackend.latencyMillis = intent.getLongExtra(EXTRA_LATENCY, fakeBackend.latencyMillis)
    }
}
