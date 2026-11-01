package com.mrd.discovery.feature.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrd.discovery.core.designsystem.components.EmptyView
import com.mrd.discovery.core.designsystem.components.ErrorView
import com.mrd.discovery.core.designsystem.components.LoadingView
import com.mrd.discovery.core.designsystem.components.MrDFilterChip
import com.mrd.discovery.core.designsystem.components.MrDSearchBar
import com.mrd.discovery.core.designsystem.components.OfflineBanner
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme

@Composable
internal fun DiscoveryScreen(
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoveryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DiscoveryScreenContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onOpenNowToggle = viewModel::onOpenNowToggle,
        onRetry = viewModel::onRetry,
        onRestaurantClick = onRestaurantClick,
        modifier = modifier,
    )
}

/** Stateless, so previews and tests can drive every state directly. */
@Composable
internal fun DiscoveryScreenContent(
    state: DiscoveryUiState,
    onQueryChange: (String) -> Unit,
    onOpenNowToggle: () -> Unit,
    onRetry: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding(),
        ) {
            DiscoveryHeader(
                state = state,
                onQueryChange = onQueryChange,
                onOpenNowToggle = onOpenNowToggle,
            )
            Box(modifier = Modifier.weight(1f)) {
                when (val content = state.content) {
                    DiscoveryContent.Loading ->
                        LoadingView(description = stringResource(R.string.discovery_loading))

                    is DiscoveryContent.Loaded -> RestaurantList(
                        content = content,
                        bottomInset = innerPadding.calculateBottomPadding(),
                        onRetry = onRetry,
                        onRestaurantClick = onRestaurantClick,
                    )

                    is DiscoveryContent.Empty -> EmptyView(
                        title = stringResource(R.string.discovery_empty_title),
                        message = stringResource(
                            if (content.filtersActive) {
                                R.string.discovery_empty_filtered_message
                            } else {
                                R.string.discovery_empty_feed_message
                            },
                        ),
                    )

                    is DiscoveryContent.Error -> ErrorView(
                        title = stringResource(R.string.discovery_error_title),
                        message = stringResource(content.kind.messageRes()),
                        retryLabel = stringResource(R.string.discovery_retry),
                        onRetry = onRetry,
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoveryHeader(state: DiscoveryUiState, onQueryChange: (String) -> Unit, onOpenNowToggle: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = MrDSpacing.md),
        verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
    ) {
        Text(
            text = stringResource(R.string.discovery_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        MrDSearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            placeholder = stringResource(R.string.discovery_search_placeholder),
        )
        MrDFilterChip(
            label = stringResource(R.string.discovery_filter_open_now),
            selected = state.openNowOnly,
            onClick = onOpenNowToggle,
        )
    }
}

@Composable
private fun RestaurantList(
    content: DiscoveryContent.Loaded,
    bottomInset: androidx.compose.ui.unit.Dp,
    onRetry: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = MrDSpacing.md,
            end = MrDSpacing.md,
            top = MrDSpacing.sm,
            bottom = bottomInset + MrDSpacing.md,
        ),
        verticalArrangement = Arrangement.spacedBy(MrDSpacing.md),
    ) {
        content.offline?.let { notice ->
            item(key = OFFLINE_KEY, contentType = OFFLINE_KEY) {
                OfflineBanner(
                    title = stringResource(R.string.discovery_offline_title),
                    message = offlineMessage(notice),
                    retryLabel = stringResource(R.string.discovery_retry),
                    onRetry = onRetry,
                )
            }
        }
        item(key = COUNT_KEY, contentType = COUNT_KEY) {
            Text(
                text = pluralStringResource(
                    R.plurals.discovery_count,
                    content.restaurants.size,
                    content.restaurants.size,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(
            items = content.restaurants,
            key = { it.id },
            contentType = { RESTAURANT_TYPE },
        ) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) },
            )
        }
    }
}

@Composable
private fun offlineMessage(notice: OfflineNotice): String = when (val minutes = notice.minutesSinceSync) {
    null -> stringResource(R.string.discovery_offline_message)
    0L -> stringResource(R.string.discovery_offline_message_now)
    else -> stringResource(R.string.discovery_offline_message_minutes, minutes)
}

private fun ErrorKind.messageRes(): Int = when (this) {
    ErrorKind.Network -> R.string.discovery_error_network
    ErrorKind.Timeout -> R.string.discovery_error_timeout
    ErrorKind.Unknown -> R.string.discovery_error_unknown
}

private const val OFFLINE_KEY = "offline"
private const val COUNT_KEY = "count"
private const val RESTAURANT_TYPE = "restaurant"

@Preview(showBackground = true)
@Composable
private fun LoadedOfflinePreview() {
    MrDTheme {
        DiscoveryScreenContent(
            state = DiscoveryUiState(
                content = DiscoveryContent.Loaded(
                    restaurants = listOf(
                        previewRestaurant(),
                        previewRestaurant(id = "r-1002", name = "Sushi on Sea Point", isOpen = false),
                    ),
                    offline = OfflineNotice(minutesSinceSync = 5),
                ),
            ),
            onQueryChange = {},
            onOpenNowToggle = {},
            onRetry = {},
            onRestaurantClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MrDTheme {
        DiscoveryScreenContent(
            state = DiscoveryUiState(content = DiscoveryContent.Error(ErrorKind.Network)),
            onQueryChange = {},
            onOpenNowToggle = {},
            onRetry = {},
            onRestaurantClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    MrDTheme {
        DiscoveryScreenContent(
            state = DiscoveryUiState(
                query = "zzz",
                content = DiscoveryContent.Empty(filtersActive = true),
            ),
            onQueryChange = {},
            onOpenNowToggle = {},
            onRetry = {},
            onRestaurantClick = {},
        )
    }
}
