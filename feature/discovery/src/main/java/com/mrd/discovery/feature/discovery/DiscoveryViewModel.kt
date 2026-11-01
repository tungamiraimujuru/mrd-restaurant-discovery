package com.mrd.discovery.feature.discovery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrd.discovery.core.common.dispatch.DefaultDispatcher
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.common.result.AppError
import com.mrd.discovery.core.common.time.Clock
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class DiscoveryViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val currencyFormatter: CurrencyFormatter,
    private val clock: Clock,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val openNowOnly = savedStateHandle.getStateFlow(KEY_OPEN_NOW, false)

    /** One set of results: the filter that produced it and the restaurants, already formatted for display. */
    private data class SearchResults(val filter: RestaurantFilter, val restaurants: List<RestaurantUiModel>)

    /**
     * Only the database query is debounced; the text field itself updates on every keystroke.
     * Formatting runs here, once per result set and off the main thread, so keystrokes and sync
     * state changes never trigger it again.
     */
    private val results: Flow<SearchResults> =
        combine(
            query.debounce { if (it.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS },
            openNowOnly,
        ) { text, openOnly -> RestaurantFilter(query = text, openNowOnly = openOnly) }
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                repository.observeRestaurants(filter).map { restaurants ->
                    SearchResults(filter, restaurants.map { it.toUiModel(currencyFormatter) })
                }
            }
            .flowOn(defaultDispatcher)

    val uiState: StateFlow<DiscoveryUiState> = combine(
        query,
        openNowOnly,
        results,
        repository.observeSyncState(),
    ) { text, openOnly, found, sync ->
        DiscoveryUiState(
            query = text,
            openNowOnly = openOnly,
            isRefreshing = sync.isRefreshing,
            content = buildContent(found, sync),
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

    fun onRefresh() = refresh()

    /** [favorite] is the state the user asked for, so a double tap cannot flip it back. */
    fun onFavoriteClick(restaurantId: String, favorite: Boolean) {
        viewModelScope.launch { repository.setFavorite(restaurantId, favorite) }
    }

    // The outcome is not used here: the repository publishes it through the sync state, which is
    // what drives the banner and the error view.
    private fun refresh() {
        viewModelScope.launch { repository.refresh() }
    }

    private fun buildContent(found: SearchResults, sync: SyncState): DiscoveryContent {
        val error = sync.error
        val lastSynced = sync.lastSyncedAtMillis
        return when {
            found.restaurants.isNotEmpty() -> DiscoveryContent.Loaded(
                restaurants = found.restaurants,
                offline = error?.let { OfflineNotice(minutesSince(lastSynced)) },
            )

            lastSynced == null ->
                if (error != null && !sync.isRefreshing) {
                    DiscoveryContent.Error(error.toErrorKind())
                } else {
                    DiscoveryContent.Loading
                }

            else -> DiscoveryContent.Empty(filtersActive = found.filter.isActive)
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
