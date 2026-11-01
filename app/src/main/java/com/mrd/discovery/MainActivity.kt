package com.mrd.discovery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.common.logging.AppLogger
import com.mrd.discovery.core.designsystem.components.MrDFilterChip
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import com.mrd.discovery.core.network.fake.FakeBackendConfig
import com.mrd.discovery.core.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var logger: AppLogger

    @Inject
    lateinit var repository: RestaurantRepository

    @Inject
    lateinit var fakeBackend: FakeBackendConfig

    @Inject
    lateinit var currencyFormatter: CurrencyFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug(TAG, "MainActivity created")
        setContent {
            MrDTheme {
                DataSmokeScreen(repository, fakeBackend) { cents -> currencyFormatter.format(cents) }
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}

/** TEMPORARY: proves cache, refresh and failure behaviour on a device. Replaced in Phase 5. */
@Composable
private fun DataSmokeScreen(
    repository: RestaurantRepository,
    fakeBackend: FakeBackendConfig,
    formatFee: (Int) -> String,
) {
    val scope = rememberCoroutineScope()
    val restaurants by remember { repository.observeRestaurants(RestaurantFilter()) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val sync by remember { repository.observeSyncState() }
        .collectAsStateWithLifecycle(initialValue = SyncState())
    var mode by remember { mutableStateOf(fakeBackend.mode) }

    LaunchedEffect(Unit) { repository.refresh() }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
        ) {
            item {
                Text(
                    text = "Data layer check",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = MrDSpacing.md),
                )
            }
            item {
                val lastSync = sync.lastSyncedAtMillis?.let { DateFormat.getTimeInstance().format(Date(it)) }
                Text(
                    text = "Cached: ${restaurants.size} | refreshing: ${sync.isRefreshing}\n" +
                        "Last sync: ${lastSync ?: "never"} | error: ${sync.error?.let {
                            it::class.simpleName
                        } ?: "none"}",
                    modifier = Modifier.padding(horizontal = MrDSpacing.md),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = MrDSpacing.md),
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
            }
            item {
                Button(
                    onClick = { scope.launch { repository.refresh() } },
                    enabled = !sync.isRefreshing,
                    modifier = Modifier.padding(horizontal = MrDSpacing.md),
                ) { Text("Refresh") }
            }
            items(restaurants, key = { it.id }) { restaurant ->
                val rating = restaurant.rating?.let { "★$it" } ?: "no rating"
                val fee = restaurant.deliveryFeeCents?.let(formatFee) ?: "fee n/a"
                val status = if (restaurant.isOpen) "OPEN" else "CLOSED"
                Text(
                    text = "${restaurant.name} | $rating | $fee | $status",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = MrDSpacing.md),
                )
            }
        }
    }
}
