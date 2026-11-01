package com.mrd.discovery.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mrd.discovery.core.database.dao.RestaurantDao
import com.mrd.discovery.core.database.entity.FavoriteEntity
import com.mrd.discovery.core.database.entity.RestaurantEntity
import com.mrd.discovery.core.database.entity.SyncMetadataEntity

/**
 * Version 1. Never add fallbackToDestructiveMigration(): the favorites table is user data, so
 * every schema change needs a real migration.
 */
@Database(
    entities = [RestaurantEntity::class, FavoriteEntity::class, SyncMetadataEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class MrDDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}
