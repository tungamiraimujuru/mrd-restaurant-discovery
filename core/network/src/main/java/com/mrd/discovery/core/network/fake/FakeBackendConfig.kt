package com.mrd.discovery.core.network.fake

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runtime switches for the in-app mock server, so every UI state can be demonstrated on demand:
 * set [mode] to Offline or ServerError, then tap Retry.
 */
@Singleton
class FakeBackendConfig @Inject constructor() {
    enum class Mode { Normal, Offline, Timeout, ServerError }

    @Volatile
    var mode: Mode = Mode.Normal

    @Volatile
    var latencyMillis: Long = DEFAULT_LATENCY_MILLIS

    private companion object {
        const val DEFAULT_LATENCY_MILLIS = 900L
    }
}
