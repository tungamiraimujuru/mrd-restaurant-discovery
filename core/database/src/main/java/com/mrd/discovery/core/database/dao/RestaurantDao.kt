package com.mrd.discovery.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mrd.discovery.core.database.entity.FavoriteEntity
import com.mrd.discovery.core.database.entity.RestaurantEntity
import com.mrd.discovery.core.database.entity.RestaurantRow
import com.mrd.discovery.core.database.entity.SyncMetadataEntity
import kotlinx.coroutines.flow.Flow

private const val SELECT_ROW = """
    SELECT r.*, (f.id IS NOT NULL) AS isFavorite
    FROM restaurants AS r
    LEFT JOIN favorites AS f ON f.id = r.id
"""

@Dao
internal abstract class RestaurantDao {
    /**
     * [pattern] is a LIKE pattern built by likePattern(), using backslash as the escape character.
     * Highest rated first; restaurants without a rating sort last (SQLite puts NULL last in DESC).
     */
    @Query(
        """
        $SELECT_ROW
        WHERE r.nameLower LIKE :pattern ESCAPE '\'
          AND (:openOnly = 0 OR r.isOpen = 1)
        ORDER BY r.rating DESC, r.name ASC
        """,
    )
    abstract fun observeRestaurants(pattern: String, openOnly: Boolean): Flow<List<RestaurantRow>>

    @Query("$SELECT_ROW WHERE r.id = :id")
    abstract fun observeRestaurant(id: String): Flow<RestaurantRow?>

    @Query("SELECT lastSyncedAtMillis FROM sync_metadata WHERE id = 1")
    abstract fun observeLastSyncedAt(): Flow<Long?>

    /** Swaps the whole cache atomically. Readers see the old list or the new one, never a mix. */
    @Transaction
    open suspend fun replaceAll(restaurants: List<RestaurantEntity>, syncedAtMillis: Long) {
        deleteAllRestaurants()
        insertRestaurants(restaurants)
        upsertSyncMetadata(SyncMetadataEntity(lastSyncedAtMillis = syncedAtMillis))
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    abstract suspend fun removeFavorite(id: String)

    @Query("DELETE FROM restaurants")
    abstract suspend fun deleteAllRestaurants()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRestaurants(restaurants: List<RestaurantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertSyncMetadata(metadata: SyncMetadataEntity)
}
