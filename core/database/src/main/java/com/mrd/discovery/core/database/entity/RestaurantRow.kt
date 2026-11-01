package com.mrd.discovery.core.database.entity

import androidx.room.Embedded

/** A restaurant joined with whether it is in the favorites table. */
internal data class RestaurantRow(@Embedded val restaurant: RestaurantEntity, val isFavorite: Boolean)
