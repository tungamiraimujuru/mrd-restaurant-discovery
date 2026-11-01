package com.mrd.discovery.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A single row recording when the cache was last refreshed successfully. */
@Entity(tableName = "sync_metadata")
internal data class SyncMetadataEntity(@PrimaryKey val id: Int = SINGLETON_ID, val lastSyncedAtMillis: Long) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
