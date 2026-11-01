package com.mrd.discovery.feature.details

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface DetailsUiState {
    /** The cache has never synced and nothing has failed yet, or a retry is in flight. */
    data object Loading : DetailsUiState

    data class Content(val restaurant: RestaurantDetailsUi) : DetailsUiState

    /** The cache is up to date and has no restaurant with this id (bad id or stale link). */
    data object NotFound : DetailsUiState

    /** Nothing is cached and the refresh failed, so existence could not be checked. */
    data object Failed : DetailsUiState
}
