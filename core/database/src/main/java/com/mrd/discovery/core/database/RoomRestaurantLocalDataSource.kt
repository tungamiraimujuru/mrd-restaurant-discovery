package com.mrd.discovery.core.database

import com.mrd.discovery.core.database.dao.RestaurantDao
import com.mrd.discovery.core.database.entity.FavoriteEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomRestaurantLocalDataSource @Inject constructor(private val dao: RestaurantDao) :
    RestaurantLocalDataSource {
    override fun observeRestaurants(nameQuery: String, openNowOnly: Boolean): Flow<List<CachedRestaurant>> =
        dao.observeRestaurants(likePattern(nameQuery), openNowOnly)
            .map { rows -> rows.map { it.toCached() } }

    override fun observeRestaurant(id: String): Flow<CachedRestaurant?> =
        dao.observeRestaurant(id).map { it?.toCached() }

    override fun observeLastSyncedAt(): Flow<Long?> = dao.observeLastSyncedAt()

    override suspend fun replaceAll(restaurants: List<CachedRestaurant>, syncedAtMillis: Long) =
        dao.replaceAll(restaurants.map { it.toEntity() }, syncedAtMillis)

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        if (favorite) dao.addFavorite(FavoriteEntity(id)) else dao.removeFavorite(id)
    }
}
