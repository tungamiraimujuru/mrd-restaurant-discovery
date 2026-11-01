package com.mrd.discovery.core.database

import kotlinx.coroutines.flow.Flow

/** The only database surface other modules see. Every read is a Flow that re-emits when data changes. */
interface RestaurantLocalDataSource {
    fun observeRestaurants(nameQuery: String, openNowOnly: Boolean): Flow<List<CachedRestaurant>>

    fun observeRestaurant(id: String): Flow<CachedRestaurant?>

    /** Epoch millis of the last successful refresh, or null if the cache has never been filled. */
    fun observeLastSyncedAt(): Flow<Long?>

    /** Atomically replaces every cached restaurant and stamps the sync time. Favorites are untouched. */
    suspend fun replaceAll(restaurants: List<CachedRestaurant>, syncedAtMillis: Long)

    suspend fun setFavorite(id: String, favorite: Boolean)
}
