package com.mrd.discovery.feature.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrd.discovery.core.designsystem.theme.MrDSpacing

/** TEMPORARY: replaced by the real list in Phase 5. */
@Composable
internal fun DiscoveryPlaceholderScreen(onRestaurantClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MrDSpacing.md),
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.md),
        ) {
            Text(text = "Discovery", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Placeholder. The real list arrives in Phase 5.")
            Button(onClick = { onRestaurantClick(SAMPLE_RESTAURANT_ID) }) {
                Text("Open Cape Town Grill")
            }
        }
    }
}

private const val SAMPLE_RESTAURANT_ID = "r-1001"
