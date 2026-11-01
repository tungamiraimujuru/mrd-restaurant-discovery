package com.mrd.discovery.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
internal data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    /** Lowercased copy of [name]; the search column, so matching is case-insensitive for all scripts. */
    val nameLower: String,
    /** Cuisines joined by [com.mrd.discovery.core.database.CuisineCodec]. */
    val cuisines: String,
    val rating: Double?,
    val deliveryFeeCents: Int?,
    val etaMinutes: Int?,
    val isOpen: Boolean,
    val imageUrl: String?,
)
