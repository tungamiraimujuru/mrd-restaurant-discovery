package com.mrd.discovery.core.database

/**
 * A restaurant as stored in the cache. Plain Kotlin, no Room types, so other modules never depend
 * on the persistence library. [isFavorite] is derived from the favorites table and ignored on write.
 */
data class CachedRestaurant(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double?,
    val deliveryFeeCents: Int?,
    val etaMinutes: Int?,
    val isOpen: Boolean,
    val imageUrl: String?,
    val isFavorite: Boolean = false,
)
