package com.mrd.discovery.feature.details

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

/** TEMPORARY: replaced by the real details screen in Phase 6. */
@Composable
internal fun DetailsPlaceholderScreen(restaurantId: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MrDSpacing.md),
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.md),
        ) {
            Text(text = "Details", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Restaurant id: $restaurantId")
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
