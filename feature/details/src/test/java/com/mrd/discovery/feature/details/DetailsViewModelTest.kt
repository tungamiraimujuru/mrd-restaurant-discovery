package com.mrd.discovery.feature.details

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
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repository = FakeRestaurantRepository()

    private fun newViewModel(id: String? = "r-1") = DetailsViewModel(
        repository = repository,
        currencyFormatter = CurrencyFormatter(),
        savedStateHandle = SavedStateHandle(if (id == null) emptyMap() else mapOf(DetailsViewModel.KEY_ID to id)),
    )

    private fun TestScope.collect(viewModel: DetailsViewModel): List<DetailsUiState> {
        val states = mutableListOf<DetailsUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { states += it }
        }
        runCurrent()
        return states
    }

    private fun restaurant(id: String = "r-1", isFavorite: Boolean = false) = Restaurant(
        id = id,
        name = "Cape Town Grill",
        cuisines = listOf("Steakhouse", "Grill"),
        rating = 4.6,
        deliveryFeeCents = 0,
        etaMinutes = 32,
        isOpen = true,
        imageUrl = null,
        isFavorite = isFavorite,
    )

    private val synced = SyncState(lastSyncedAtMillis = 1_000L)

    @Test
    fun showsTheCachedRestaurant() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant())
        repository.syncState.value = synced
        val states = collect(newViewModel())

        val content = states.last() as DetailsUiState.Content
        assertEquals("Cape Town Grill", content.restaurant.name)
        assertEquals("Steakhouse · Grill", content.restaurant.cuisines)
        assertEquals("4.6", content.restaurant.ratingText)
    }

    @Test
    fun carriesTheFavoriteFlagThrough() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant(isFavorite = true))
        repository.syncState.value = synced
        val states = collect(newViewModel())

        assertTrue((states.last() as DetailsUiState.Content).restaurant.isFavorite)
    }

    @Test
    fun showsNotFoundWhenTheCacheIsFreshAndHasNoSuchRestaurant() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant(id = "other"))
        repository.syncState.value = synced
        val states = collect(newViewModel(id = "r-404"))

        assertEquals(DetailsUiState.NotFound, states.last())
    }

    @Test
    fun aMissingIdIsNotFoundInsteadOfACrash() = runTest(mainRule.dispatcher) {
        repository.syncState.value = synced
        val states = collect(newViewModel(id = null))

        assertEquals(DetailsUiState.NotFound, states.last())
    }

    @Test
    fun showsLoadingWhileTheFirstSyncRuns() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState(isRefreshing = true)
        val states = collect(newViewModel())

        assertEquals(DetailsUiState.Loading, states.last())
    }

    @Test
    fun showsFailedWhenNothingIsCachedAndRefreshFailed() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState(error = AppError.Network())
        val states = collect(newViewModel())

        assertEquals(DetailsUiState.Failed, states.last())
    }

    @Test
    fun refreshesOnCreationOnlyWhenTheCacheHasNeverSynced() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState()
        newViewModel()
        runCurrent()
        assertEquals(1, repository.refreshCount)
    }

    @Test
    fun doesNotRefreshWhenTheCacheIsAlreadyFilled() = runTest(mainRule.dispatcher) {
        repository.syncState.value = synced
        newViewModel()
        runCurrent()
        assertEquals(0, repository.refreshCount)
    }

    @Test
    fun retryRefreshesAgain() = runTest(mainRule.dispatcher) {
        repository.syncState.value = synced
        val viewModel = newViewModel()
        runCurrent()

        viewModel.onRetry()
        runCurrent()

        assertEquals(1, repository.refreshCount)
    }

    @Test
    fun savedStateKeyMatchesTheRouteArgumentName() {
        assertEquals(DetailsViewModel.KEY_ID, DetailsRoute.serializer().descriptor.getElementName(0))
    }

    @Test
    fun favoriteClickFlipsTheCurrentState() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant(isFavorite = false))
        repository.syncState.value = synced
        val viewModel = newViewModel()
        collect(viewModel)

        viewModel.onFavoriteClick()
        runCurrent()

        assertEquals(listOf("r-1" to true), repository.favoriteCalls)
    }

    @Test
    fun favoriteClickOnAFavoriteRemovesIt() = runTest(mainRule.dispatcher) {
        repository.restaurants.value = listOf(restaurant(isFavorite = true))
        repository.syncState.value = synced
        val viewModel = newViewModel()
        collect(viewModel)

        viewModel.onFavoriteClick()
        runCurrent()

        assertEquals(listOf("r-1" to false), repository.favoriteCalls)
    }

    @Test
    fun favoriteClickIsIgnoredUntilTheRestaurantIsLoaded() = runTest(mainRule.dispatcher) {
        repository.syncState.value = SyncState(isRefreshing = true)
        val viewModel = newViewModel()
        collect(viewModel)

        viewModel.onFavoriteClick()
        runCurrent()

        assertTrue(repository.favoriteCalls.isEmpty())
    }
}

private class FakeRestaurantRepository : RestaurantRepository {
    val restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val syncState = MutableStateFlow(SyncState())
    var refreshCount = 0

    override fun observeRestaurants(filter: RestaurantFilter): Flow<List<Restaurant>> = restaurants

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
