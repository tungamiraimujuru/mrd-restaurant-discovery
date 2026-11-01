package com.mrd.discovery.feature.discovery

import androidx.lifecycle.SavedStateHandle
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.common.result.AppError
import com.mrd.discovery.core.common.result.AppResult
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoveryViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repository = FakeRestaurantRepository()
    private var now = 10 * 60_000L

    private fun newViewModel(handle: SavedStateHandle = SavedStateHandle()) =
        DiscoveryViewModel(repository, CurrencyFormatter(), { now }, handle)

    private fun TestScope.collect(viewModel: DiscoveryViewModel): List<DiscoveryUiState> {
        val states = mutableListOf<DiscoveryUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { states += it }
        }
        runCurrent()
        return states
    }

    private fun restaurant(id: String, name: String, isOpen: Boolean = true) = Restaurant(
        id = id,
        name = name,
        cuisines = listOf("Test"),
        rating = 4.0,
        deliveryFeeCents = 1500,
        etaMinutes = 30,
        isOpen = isOpen,
        imageUrl = null,
        isFavorite = false,
    )

    private fun synced(at: Long = now) = SyncState(lastSyncedAtMillis = at)

    @Test
    fun showsLoadingUntilTheFirstSyncCompletes() = runTest(mainRule.dispatcher) {
        val states = collect(newViewModel())

        assertEquals(DiscoveryContent.Loading, states.last().content)
    }

    @Test
    fun showsCachedRestaurantsOnceTheyExist() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Sushi"), restaurant("2", "Grill"))
        repository.syncState.value = synced()
        val states = collect(newViewModel())

        val content = states.last().content as DiscoveryContent.Loaded
        assertEquals(listOf("Sushi", "Grill"), content.restaurants.map { it.name })
        assertNull(content.offline)
    }

    @Test
    fun showsErrorWhenNothingWasEverCachedAndRefreshFailed() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState(error = AppError.Network())
        val states = collect(newViewModel())

        assertEquals(DiscoveryContent.Error(ErrorKind.Network), states.last().content)
    }

    @Test
    fun retryAfterAnErrorShowsLoadingAgain() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState(isRefreshing = true, error = AppError.Network())
        val states = collect(newViewModel())

        assertEquals(DiscoveryContent.Loading, states.last().content)
    }

    @Test
    fun showsCachedDataWithAnOfflineNoticeWhenRefreshFails() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Sushi"))
        repository.syncState.value = SyncState(lastSyncedAtMillis = now - 5 * 60_000L, error = AppError.Timeout())
        val states = collect(newViewModel())

        val content = states.last().content as DiscoveryContent.Loaded
        assertEquals(1, content.restaurants.size)
        assertEquals(OfflineNotice(minutesSinceSync = 5), content.offline)
    }

    @Test
    fun showsEmptyWithFiltersActiveWhenNothingMatches() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Sushi"))
        repository.syncState.value = synced()
        val viewModel = newViewModel()
        val states = collect(viewModel)

        viewModel.onQueryChange("zzz")
        advanceTimeBy(DiscoveryViewModel.SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertEquals(DiscoveryContent.Empty(filtersActive = true), states.last().content)
    }

    @Test
    fun showsEmptyWithoutFiltersWhenTheFeedIsEmpty() = runTest(mainRule.dispatcher) {
        repository.syncState.value = synced()
        val states = collect(newViewModel())

        assertEquals(DiscoveryContent.Empty(filtersActive = false), states.last().content)
    }

    @Test
    fun queryUpdatesImmediatelyButTheSearchIsDebounced() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Sushi"), restaurant("2", "Grill"))
        repository.syncState.value = synced()
        val viewModel = newViewModel()
        val states = collect(viewModel)

        viewModel.onQueryChange("sushi")
        runCurrent()
        assertEquals("sushi", states.last().query)
        assertEquals(2, (states.last().content as DiscoveryContent.Loaded).restaurants.size)

        advanceTimeBy(DiscoveryViewModel.SEARCH_DEBOUNCE_MILLIS - 1)
        runCurrent()
        assertEquals(2, (states.last().content as DiscoveryContent.Loaded).restaurants.size)

        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, (states.last().content as DiscoveryContent.Loaded).restaurants.size)
    }

    @Test
    fun openNowToggleFiltersAndIsSaved() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Open", true), restaurant("2", "Shut", false))
        repository.syncState.value = synced()
        val handle = SavedStateHandle()
        val viewModel = newViewModel(handle)
        val states = collect(viewModel)

        viewModel.onOpenNowToggle()
        runCurrent()

        assertTrue(states.last().openNowOnly)
        assertEquals(true, handle.get<Boolean>(DiscoveryViewModel.KEY_OPEN_NOW))
        assertEquals(1, (states.last().content as DiscoveryContent.Loaded).restaurants.size)
    }

    @Test
    fun queryAndFilterAreRestoredFromSavedState() = runTest(mainRule.dispatcher) {
        val handle = SavedStateHandle(
            mapOf(DiscoveryViewModel.KEY_QUERY to "sushi", DiscoveryViewModel.KEY_OPEN_NOW to true),
        )
        val states = collect(newViewModel(handle))

        assertEquals("sushi", states.first().query)
        assertTrue(states.first().openNowOnly)
    }

    @Test
    fun refreshesOnCreationAndAgainOnRetry() = runTest(mainRule.dispatcher) {
        val viewModel = newViewModel()
        runCurrent()
        assertEquals(1, repository.refreshCount)

        viewModel.onRefresh()
        runCurrent()
        assertEquals(2, repository.refreshCount)
    }

    @Test
    fun refreshIndicatorFollowsSyncProgress() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant("1", "Sushi"))
        repository.syncState.value = SyncState(isRefreshing = true, lastSyncedAtMillis = now)
        val states = collect(newViewModel())

        assertTrue(states.last().isRefreshing)
        assertTrue(states.last().content is DiscoveryContent.Loaded)

        repository.syncState.value = synced()
        runCurrent()
        assertFalse(states.last().isRefreshing)
    }

    @Test
    fun favoriteClickSavesTheRequestedState() = runTest(mainRule.dispatcher) {
        val viewModel = newViewModel()
        runCurrent()

        viewModel.onFavoriteClick("r-1", true)
        viewModel.onFavoriteClick("r-1", true)
        runCurrent()

        assertEquals(listOf("r-1" to true, "r-1" to true), repository.favoriteCalls)
    }
}

private class FakeRestaurantRepository : RestaurantRepository {
    val restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val syncState = MutableStateFlow(SyncState(isRefreshing = true))
    var refreshCount = 0

    override fun observeRestaurants(filter: RestaurantFilter): Flow<List<Restaurant>> = restaurants.map { list ->
        list.filter {
            it.name.contains(filter.query.trim(), ignoreCase = true) && (!filter.openNowOnly || it.isOpen)
        }
    }

    override fun observeRestaurant(id: String): Flow<Restaurant?> =
        restaurants.map { list -> list.firstOrNull { it.id == id } }

    override fun observeSyncState(): Flow<SyncState> = syncState

    override suspend fun refresh(): AppResult<Unit> {
        refreshCount++
        return AppResult.Success(Unit)
    }

    val favoriteCalls = mutableListOf<Pair<String, Boolean>>()

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        favoriteCalls += id to favorite
    }
}
