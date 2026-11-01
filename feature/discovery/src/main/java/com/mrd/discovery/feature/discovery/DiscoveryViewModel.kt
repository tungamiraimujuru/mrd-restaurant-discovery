package com.mrd.discovery.feature.discovery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.common.result.AppError
import com.mrd.discovery.core.common.time.Clock
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class DiscoveryViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val currencyFormatter: CurrencyFormatter,
    private val clock: Clock,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val openNowOnly = savedStateHandle.getStateFlow(KEY_OPEN_NOW, false)

    /** Only the database query is debounced; the text field itself updates on every keystroke. */
    private val results: Flow<Pair<RestaurantFilter, List<Restaurant>>> =
        combine(
            query.debounce { if (it.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS },
            openNowOnly,
        ) { text, openOnly -> RestaurantFilter(query = text, openNowOnly = openOnly) }
            .distinctUntilChanged()
            .flatMapLatest { filter -> repository.observeRestaurants(filter).map { filter to it } }

    val uiState: StateFlow<DiscoveryUiState> = combine(
        query,
        openNowOnly,
        results,
        repository.observeSyncState(),
    ) { text, openOnly, (filter, restaurants), sync ->
        DiscoveryUiState(
            query = text,
            openNowOnly = openOnly,
            isRefreshing = sync.isRefreshing,
            content = buildContent(filter, restaurants, sync),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DiscoveryUiState(query = query.value, openNowOnly = openNowOnly.value),
    )

    init {
        refresh()
    }

    fun onQueryChange(newQuery: String) {
        savedStateHandle[KEY_QUERY] = newQuery
    }

    fun onOpenNowToggle() {
        savedStateHandle[KEY_OPEN_NOW] = !openNowOnly.value
    }

    fun onRefresh() = refresh() // was onRetry(); the banner, error view and pull gesture all use it

    // The outcome is not used here: the repository publishes it through the sync state, which is
    // what drives the banner and the error view.
    private fun refresh() {
        viewModelScope.launch { repository.refresh() }
    }

    private fun buildContent(
        filter: RestaurantFilter,
        restaurants: List<Restaurant>,
        sync: SyncState,
    ): DiscoveryContent {
        val error = sync.error
        val lastSynced = sync.lastSyncedAtMillis
        return when {
            restaurants.isNotEmpty() -> DiscoveryContent.Loaded(
                restaurants = restaurants.map { it.toUiModel(currencyFormatter) },
                offline = error?.let { OfflineNotice(minutesSince(lastSynced)) },
            )

            lastSynced == null ->
                if (error != null && !sync.isRefreshing) {
                    DiscoveryContent.Error(error.toErrorKind())
                } else {
                    DiscoveryContent.Loading
                }

            else -> DiscoveryContent.Empty(filtersActive = filter.isActive)
        }
    }

    private fun minutesSince(timestampMillis: Long?): Long? =
        timestampMillis?.let { ((clock.nowMillis() - it) / MILLIS_PER_MINUTE).coerceAtLeast(0) }

    private fun AppError.toErrorKind() = when (this) {
        is AppError.Network -> ErrorKind.Network
        is AppError.Timeout -> ErrorKind.Timeout
        is AppError.Unknown -> ErrorKind.Unknown
    }

    companion object {
        internal const val KEY_QUERY = "query"
        internal const val KEY_OPEN_NOW = "open_now"
        internal const val SEARCH_DEBOUNCE_MILLIS = 250L
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val MILLIS_PER_MINUTE = 60_000L
    }
}
