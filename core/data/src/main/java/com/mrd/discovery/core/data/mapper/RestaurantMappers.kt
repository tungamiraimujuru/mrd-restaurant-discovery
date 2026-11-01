package com.mrd.discovery.core.data.mapper

import com.mrd.discovery.core.database.CachedRestaurant
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.network.dto.RestaurantDto

private val VALID_RATING = 0.0..5.0

/**
 * Turns a raw network record into something safe to cache, or null when the record cannot be
 * shown at all (no id or no name). Implausible optional values become "unknown" rather than
 * being displayed as nonsense.
 */
internal fun RestaurantDto.toCachedOrNull(): CachedRestaurant? {
    val safeId = id?.trim().orEmpty()
    val safeName = name?.trim().orEmpty()
    if (safeId.isEmpty() || safeName.isEmpty()) return null

    return CachedRestaurant(
        id = safeId,
        name = safeName,
        cuisines = cuisines.orEmpty().map { it.trim() }.filter { it.isNotEmpty() },
        rating = rating?.takeIf { it in VALID_RATING },
        deliveryFeeCents = deliveryFeeCents?.takeIf { it >= 0 },
        etaMinutes = etaMinutes?.takeIf { it > 0 },
        // Unknown opening state is treated as closed: better to under-promise.
        isOpen = isOpen ?: false,
        imageUrl = imageUrl?.trim()?.takeIf { it.isNotEmpty() },
    )
}

internal fun CachedRestaurant.toDomain() = Restaurant(
    id = id,
    name = name,
    cuisines = cuisines,
    rating = rating,
    deliveryFeeCents = deliveryFeeCents,
    etaMinutes = etaMinutes,
    isOpen = isOpen,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
)
