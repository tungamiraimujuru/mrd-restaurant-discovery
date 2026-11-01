package com.mrd.discovery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.common.logging.AppLogger
import com.mrd.discovery.core.designsystem.components.InfoChip
import com.mrd.discovery.core.designsystem.components.MrDFilterChip
import com.mrd.discovery.core.designsystem.components.MrDSearchBar
import com.mrd.discovery.core.designsystem.components.OfflineBanner
import com.mrd.discovery.core.designsystem.components.RatingPill
import com.mrd.discovery.core.designsystem.components.StatusBadge
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var logger: AppLogger

    @Inject
    lateinit var currencyFormatter: CurrencyFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug(TAG, "MainActivity created")
        setContent {
            MrDTheme {
                FoundationShell(deliveryFee = currencyFormatter.format(1500))
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}

/** Temporary screen proving theme, edge-to-edge and injection. Replaced by navigation in Phase 4. */
@Composable
private fun FoundationShell(deliveryFee: String) {
    var query by rememberSaveable { mutableStateOf("") }
    var openNow by rememberSaveable { mutableStateOf(true) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MrDSpacing.md)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.md),
        ) {
            Text(text = "Mr D Restaurants", style = MaterialTheme.typography.headlineMedium)
            MrDSearchBar(query = query, onQueryChange = { query = it }, placeholder = "Search restaurants")
            MrDFilterChip(label = "Open now", selected = openNow, onClick = { openNow = !openNow })
            OfflineBanner(
                title = "Working offline",
                message = "Showing saved restaurants",
                retryLabel = "Retry",
                onRetry = {},
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MrDSpacing.sm)) {
                StatusBadge(text = "OPEN", isOpen = true)
                StatusBadge(text = "CLOSED", isOpen = false)
                RatingPill(text = "4.6")
                InfoChip(text = "$deliveryFee delivery")
            }
        }
    }
}
