package com.mrd.discovery.debug

import android.content.Intent
import com.mrd.discovery.core.network.fake.FakeBackendConfig

/** Release builds ignore launch extras; this keeps the call site identical across variants. */
@Suppress("UNUSED_PARAMETER")
fun applyDebugLaunchOverrides(intent: Intent, fakeBackend: FakeBackendConfig) = Unit
