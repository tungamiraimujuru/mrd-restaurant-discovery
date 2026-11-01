package com.mrd.discovery.feature.discovery

import androidx.compose.runtime.Immutable

/** The controls are always visible; only [content] changes underneath them. */
@Immutable
internal data class DiscoveryUiState(
    val query: String = "",
    val openNowOnly: Boolean = false,
    val isRefreshing: Boolean = false,
    val content: DiscoveryContent = DiscoveryContent.Loading,
)

internal sealed interface DiscoveryContent {
    /** Nothing has ever synced and nothing has failed yet, or a retry is in flight. */
    data object Loading : DiscoveryContent

    /** [offline] is set when the last refresh failed but cached data is still on screen. */
    data class Loaded(val restaurants: List<RestaurantUiModel>, val offline: OfflineNotice?) : DiscoveryContent

    /** The cache has synced, but nothing matches. [filtersActive] tells "no matches" from "empty feed". */
    data class Empty(val filtersActive: Boolean) : DiscoveryContent

    /** No cached data exists and the refresh failed. */
    data class Error(val kind: ErrorKind) : DiscoveryContent
}

internal data class OfflineNotice(val minutesSinceSync: Long?)

internal enum class ErrorKind { Network, Timeout, Unknown }
