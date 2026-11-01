package com.mrd.discovery.core.data

import com.mrd.discovery.core.common.dispatch.IoDispatcher
import com.mrd.discovery.core.common.logging.AppLogger
import com.mrd.discovery.core.common.result.AppError
import com.mrd.discovery.core.common.result.AppResult
import com.mrd.discovery.core.common.result.appResultOf
import com.mrd.discovery.core.common.time.Clock
import com.mrd.discovery.core.data.mapper.toCachedOrNull
import com.mrd.discovery.core.data.mapper.toDomain
import com.mrd.discovery.core.database.CachedRestaurant
import com.mrd.discovery.core.database.RestaurantLocalDataSource
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import com.mrd.discovery.core.network.RestaurantRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Every read comes from the local cache; [refresh] is the only code that touches the network.
 * A failed refresh leaves the cache exactly as it was and is reported through [observeSyncState].
 */
@Singleton
internal class OfflineFirstRestaurantRepository @Inject constructor(
    private val remote: RestaurantRemoteDataSource,
    private val local: RestaurantLocalDataSource,
    private val clock: Clock,
    private val logger: AppLogger,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : RestaurantRepository {
    private data class RefreshState(val isRefreshing: Boolean = false, val error: AppError? = null)

    private val refreshState = MutableStateFlow(RefreshState())
    private val refreshLock = Mutex()

    override fun observeRestaurants(filter: RestaurantFilter): Flow<List<Restaurant>> =
        local.observeRestaurants(filter.query, filter.openNowOnly)
            .map { cached -> cached.map { it.toDomain() } }

    override fun observeRestaurant(id: String): Flow<Restaurant?> = local.observeRestaurant(id).map { it?.toDomain() }

    override fun observeSyncState(): Flow<SyncState> =
        combine(local.observeLastSyncedAt(), refreshState) { lastSynced, refresh ->
            SyncState(
                isRefreshing = refresh.isRefreshing,
                lastSyncedAtMillis = lastSynced,
                error = refresh.error,
            )
        }

    override suspend fun refresh(): AppResult<Unit> = refreshLock.withLock {
        refreshState.update { it.copy(isRefreshing = true) }
        // If the caller is cancelled the previous error is kept and "refreshing" is cleared.
        var outcome = refreshState.value.copy(isRefreshing = false)
        try {
            val result = withContext(ioDispatcher) { fetchAndStore() }
            if (result is AppResult.Failure) {
                logger.error(TAG, "Refresh failed: ${result.error::class.simpleName}", result.error.cause)
            }
            outcome = RefreshState(error = (result as? AppResult.Failure)?.error)
            result
        } finally {
            refreshState.value = outcome
        }
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) = local.setFavorite(id, favorite)

    private suspend fun fetchAndStore(): AppResult<Unit> = appResultOf {
        val raw = remote.fetchRestaurants()
        val valid: List<CachedRestaurant> = raw.mapNotNull { it.toCachedOrNull() }.distinctBy { it.id }
        // Records arrived but none were usable: a broken feed must not erase a good cache.
        check(valid.isNotEmpty() || raw.isEmpty()) { "Feed contained no valid restaurants" }
        local.replaceAll(valid, clock.nowMillis())
    }

    private companion object {
        const val TAG = "RestaurantRepository"
    }
}
