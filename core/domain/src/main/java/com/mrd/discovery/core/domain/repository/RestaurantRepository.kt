package com.mrd.discovery.core.domain.repository

import com.mrd.discovery.core.common.result.AppResult
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.domain.model.RestaurantFilter
import com.mrd.discovery.core.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

/**
 * Offline-first contract: every read comes from the local cache, [refresh] updates the cache
 * from the network. Callers never see exceptions, only [AppResult].
 */
interface RestaurantRepository {
    /** Cached restaurants matching [filter], re-emitted whenever the cache or favorites change. */
    fun observeRestaurants(filter: RestaurantFilter): Flow<List<Restaurant>>

    /** A single cached restaurant, or null if it is not in the cache (bad id or deep link). */
    fun observeRestaurant(id: String): Flow<Restaurant?>

    fun observeSyncState(): Flow<SyncState>

    /** Fetches fresh data. On failure the existing cache is kept untouched. */
    suspend fun refresh(): AppResult<Unit>

    suspend fun setFavorite(id: String, favorite: Boolean)
}
