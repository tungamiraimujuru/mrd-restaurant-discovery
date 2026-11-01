package com.mrd.discovery.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme

@Composable
fun LoadingView(description: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyView(title: String, message: String, modifier: Modifier = Modifier) {
    MessageContent(title = title, message = message, modifier = modifier)
}

@Composable
fun ErrorView(title: String, message: String, retryLabel: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    MessageContent(title = title, message = message, modifier = modifier) {
        Button(onClick = onRetry) { Text(retryLabel) }
    }
}

/** Shown above cached content when a refresh failed: the data is real, just not fresh. */
@Composable
fun OfflineBanner(
    title: String,
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryFixed,
        contentColor = MaterialTheme.colorScheme.onSecondaryFixed,
        border = BorderStroke(MrDSpacing.border, MaterialTheme.colorScheme.secondaryFixedDim),
    ) {
        Row(
            modifier = Modifier.padding(start = MrDSpacing.md, end = MrDSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = MrDSpacing.sm),
            ) {
                Text(text = title, style = MaterialTheme.typography.labelSmall)
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(onClick = onRetry) { Text(retryLabel) }
        }
    }
}

@Composable
private fun MessageContent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MrDSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm, Alignment.CenterVertically),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        action?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun OfflineBannerPreview() {
    MrDTheme {
        OfflineBanner(
            title = "Working offline",
            message = "Showing saved restaurants (5 min ago)",
            retryLabel = "Retry",
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    MrDTheme {
        ErrorView(
            title = "Something went wrong",
            message = "Check your connection and try again.",
            retryLabel = "Retry",
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyViewPreview() {
    MrDTheme {
        EmptyView(title = "No restaurants found", message = "Try a different search.")
    }
}
