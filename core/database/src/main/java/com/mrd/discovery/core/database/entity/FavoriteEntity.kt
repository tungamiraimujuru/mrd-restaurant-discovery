package com.mrd.discovery.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Deliberately a separate table with no foreign key: refreshing restaurants never touches it,
 * and a favorite survives a restaurant leaving and later returning to the feed.
 */
@Entity(tableName = "favorites")
internal data class FavoriteEntity(@PrimaryKey val id: String)
