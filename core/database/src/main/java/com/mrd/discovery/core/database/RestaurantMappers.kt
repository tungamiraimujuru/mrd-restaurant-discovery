package com.mrd.discovery.core.database

import com.mrd.discovery.core.database.entity.RestaurantEntity
import com.mrd.discovery.core.database.entity.RestaurantRow
import java.util.Locale

internal fun CachedRestaurant.toEntity() = RestaurantEntity(
    id = id,
    name = name,
    nameLower = name.lowercase(Locale.ROOT),
    cuisines = CuisineCodec.encode(cuisines),
    rating = rating,
    deliveryFeeCents = deliveryFeeCents,
    etaMinutes = etaMinutes,
    isOpen = isOpen,
    imageUrl = imageUrl,
)

internal fun RestaurantRow.toCached() = CachedRestaurant(
    id = restaurant.id,
    name = restaurant.name,
    cuisines = CuisineCodec.decode(restaurant.cuisines),
    rating = restaurant.rating,
    deliveryFeeCents = restaurant.deliveryFeeCents,
    etaMinutes = restaurant.etaMinutes,
    isOpen = restaurant.isOpen,
    imageUrl = restaurant.imageUrl,
    isFavorite = isFavorite,
)
