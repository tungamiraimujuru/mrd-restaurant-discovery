package com.mrd.discovery.core.data

import com.mrd.discovery.core.common.logging.AppLogger
import com.mrd.discovery.core.common.result.AppError
import com.mrd.discovery.core.common.result.AppResult
import com.mrd.discovery.core.common.time.Clock
import com.mrd.discovery.core.database.CachedRestaurant
import com.mrd.discovery.core.database.RestaurantLocalDataSource
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import com.mrd.discovery.core.network.HttpFailureException
import com.mrd.discovery.core.network.RestaurantRemoteDataSource
import com.mrd.discovery.core.network.dto.RestaurantDto
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineFirstRestaurantRepositoryTest {
    private val remote = FakeRemote()
    private val local = FakeLocal()
    private val logger = RecordingLogger()

    private fun TestScope.repository() = OfflineFirstRestaurantRepository(
        remote = remote,
        local = local,
        clock = Clock { 1_000L },
        logger = logger,
        ioDispatcher = StandardTestDispatcher(testScheduler),
    )

    private fun dto(id: String, name: String) = RestaurantDto(id = id, name = name)

    private suspend fun names(repo: RestaurantRepository) =
        repo.observeRestaurants(RestaurantFilter()).first().map { it.name }

    @Test
    fun successfulRefreshFillsTheCacheAndStampsTheTime() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "A"), dto("2", "B")) }

        val result = repo.refresh()

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(listOf("A", "B"), names(repo))
        assertEquals(1_000L, repo.observeSyncState().first().lastSyncedAtMillis)
    }

    @Test
    fun failedRefreshKeepsTheExistingCacheAndReportsTheError() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "A")) }
        repo.refresh()

        remote.respond { throw HttpFailureException(503) }
        val result = repo.refresh()

        assertTrue((result as AppResult.Failure).error is AppError.Network)
        assertEquals(listOf("A"), names(repo))
        val sync = repo.observeSyncState().first()
        assertTrue(sync.error is AppError.Network)
        assertEquals(1_000L, sync.lastSyncedAtMillis)
        assertEquals(1, logger.errors.size)
    }

    @Test
    fun aSuccessfulRefreshClearsThePreviousError() = runTest {
        val repo = repository()
        remote.respond { throw IOException("offline") }
        repo.refresh()
        assertNotNull(repo.observeSyncState().first().error)

        remote.respond { listOf(dto("1", "A")) }
        repo.refresh()

        assertNull(repo.observeSyncState().first().error)
    }

    @Test
    fun aFeedWithNoUsableRecordsDoesNotWipeTheCache() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "A")) }
        repo.refresh()

        remote.respond { listOf(RestaurantDto(rating = 4.0), RestaurantDto(name = "No id")) }
        val result = repo.refresh()

        assertTrue((result as AppResult.Failure).error is AppError.Unknown)
        assertEquals(listOf("A"), names(repo))
    }

    @Test
    fun aGenuinelyEmptyFeedClearsTheCache() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "A")) }
        repo.refresh()

        remote.respond { emptyList() }
        val result = repo.refresh()

        assertEquals(AppResult.Success(Unit), result)
        assertTrue(names(repo).isEmpty())
    }

    @Test
    fun duplicateIdsAreCollapsed() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "First"), dto("1", "Second")) }

        repo.refresh()

        assertEquals(listOf("First"), names(repo))
    }

    @Test
    fun syncStateShowsRefreshingWhileTheRequestIsInFlight() = runTest {
        val repo = repository()
        val gate = CompletableDeferred<List<RestaurantDto>>()
        remote.respond { gate.await() }

        val job = launch { repo.refresh() }
        runCurrent()
        assertTrue(repo.observeSyncState().first().isRefreshing)

        gate.complete(listOf(dto("1", "A")))
        job.join()
        assertFalse(repo.observeSyncState().first().isRefreshing)
    }

    @Test
    fun aCancelledRefreshDoesNotLeaveTheStateStuckOnRefreshing() = runTest {
        val repo = repository()
        remote.respond { CompletableDeferred<List<RestaurantDto>>().await() }

        val job = launch { repo.refresh() }
        runCurrent()
        assertTrue(repo.observeSyncState().first().isRefreshing)

        job.cancelAndJoin()
        assertFalse(repo.observeSyncState().first().isRefreshing)
    }

    @Test
    fun favoriteToggleIsReflectedInObservedRestaurants() = runTest {
        val repo = repository()
        remote.respond { listOf(dto("1", "A")) }
        repo.refresh()

        repo.setFavorite("1", true)

        assertTrue(repo.observeRestaurants(RestaurantFilter()).first().single().isFavorite)
    }
}

private class FakeRemote : RestaurantRemoteDataSource {
    private var handler: suspend () -> List<RestaurantDto> = { emptyList() }

    fun respond(block: suspend () -> List<RestaurantDto>) {
        handler = block
    }

    override suspend fun fetchRestaurants(): List<RestaurantDto> = handler()
}

private class FakeLocal : RestaurantLocalDataSource {
    private val stored = MutableStateFlow<List<CachedRestaurant>>(emptyList())
    private val favorites = MutableStateFlow<Set<String>>(emptySet())
    private val lastSynced = MutableStateFlow<Long?>(null)

    override fun observeRestaurants(nameQuery: String, openNowOnly: Boolean): Flow<List<CachedRestaurant>> =
        combine(stored, favorites) { list, favs ->
            list.filter { it.name.contains(nameQuery, ignoreCase = true) && (!openNowOnly || it.isOpen) }
                .map { it.copy(isFavorite = it.id in favs) }
        }

    override fun observeRestaurant(id: String): Flow<CachedRestaurant?> = combine(stored, favorites) { list, favs ->
        list.firstOrNull { it.id == id }?.copy(isFavorite = id in favs)
    }

    override fun observeLastSyncedAt(): Flow<Long?> = lastSynced

    override suspend fun replaceAll(restaurants: List<CachedRestaurant>, syncedAtMillis: Long) {
        stored.value = restaurants
        lastSynced.value = syncedAtMillis
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        favorites.value = if (favorite) favorites.value + id else favorites.value - id
    }
}

private class RecordingLogger : AppLogger {
    val errors = mutableListOf<String>()

    override fun debug(tag: String, message: String) = Unit

    override fun error(tag: String, message: String, throwable: Throwable?) {
        errors += message
    }
}
