package com.mrd.discovery.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrd.discovery.core.network.fake.FakeBackendConfig

/** Release builds have no debug controls; this keeps the call site identical across variants. */
@Suppress("UNUSED_PARAMETER")
@Composable
fun DebugOverlay(fakeBackend: FakeBackendConfig, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) { content() }
}
