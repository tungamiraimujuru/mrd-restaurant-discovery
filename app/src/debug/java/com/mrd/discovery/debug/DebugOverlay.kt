package com.mrd.discovery.debug

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrd.discovery.core.designsystem.components.MrDFilterChip
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.network.fake.FakeBackendConfig

private val LatencyOptions = listOf("Fast (0 ms)" to 0L, "Normal (900 ms)" to 900L, "Slow (3 s)" to 3_000L)

/** Debug builds only: a floating button that controls how the mock backend behaves. */
@Composable
fun DebugOverlay(fakeBackend: FakeBackendConfig, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier) {
        content()
        SmallFloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(MrDSpacing.md),
        ) {
            Text("DEV")
        }
        if (showDialog) {
            BackendDialog(fakeBackend = fakeBackend, onDismiss = { showDialog = false })
        }
    }
}

@Composable
private fun BackendDialog(fakeBackend: FakeBackendConfig, onDismiss: () -> Unit) {
    var mode by remember { mutableStateOf(fakeBackend.mode) }
    var latency by remember { mutableLongStateOf(fakeBackend.latencyMillis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Mock backend") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm)) {
                Text("Response", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
                ) {
                    FakeBackendConfig.Mode.entries.forEach { option ->
                        MrDFilterChip(
                            label = option.name,
                            selected = mode == option,
                            onClick = {
                                mode = option
                                fakeBackend.mode = option
                            },
                        )
                    }
                }
                Text("Latency", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
                ) {
                    LatencyOptions.forEach { (label, millis) ->
                        MrDFilterChip(
                            label = label,
                            selected = latency == millis,
                            onClick = {
                                latency = millis
                                fakeBackend.latencyMillis = millis
                            },
                        )
                    }
                }
                Text(
                    text = "A change applies to the next refresh. Pull down on the list or tap Retry to try it.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
    )
}
