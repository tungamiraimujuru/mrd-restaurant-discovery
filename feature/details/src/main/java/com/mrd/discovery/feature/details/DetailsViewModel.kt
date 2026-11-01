package com.mrd.discovery.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class DetailsViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val currencyFormatter: CurrencyFormatter,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Navigation stores route arguments in the handle under the property names of DetailsRoute.
    private val restaurantId = savedStateHandle.get<String>(KEY_ID).orEmpty()

    val uiState: StateFlow<DetailsUiState> = combine(
        repository.observeRestaurant(restaurantId),
        repository.observeSyncState(),
    ) { restaurant, sync -> buildState(restaurant, sync) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DetailsUiState.Loading,
        )

    init {
        // A deep link can open this screen before the list ever loaded, so fill the cache if empty.
        viewModelScope.launch {
            if (repository.observeSyncState().first().lastSyncedAtMillis == null) repository.refresh()
        }
    }

    fun onRetry() {
        viewModelScope.launch { repository.refresh() }
    }

    fun onFavoriteClick() {
        val current = (uiState.value as? DetailsUiState.Content)?.restaurant ?: return
        viewModelScope.launch { repository.setFavorite(restaurantId, !current.isFavorite) }
    }

    private fun buildState(restaurant: Restaurant?, sync: SyncState): DetailsUiState = when {
        restaurant != null -> DetailsUiState.Content(restaurant.toDetailsUi(currencyFormatter))
        sync.lastSyncedAtMillis != null -> DetailsUiState.NotFound
        sync.error != null && !sync.isRefreshing -> DetailsUiState.Failed
        else -> DetailsUiState.Loading
    }

    companion object {
        internal const val KEY_ID = "id"
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
