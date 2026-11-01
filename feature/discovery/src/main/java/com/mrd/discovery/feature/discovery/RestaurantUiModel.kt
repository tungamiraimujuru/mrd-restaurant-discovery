package com.mrd.discovery.feature.discovery

import androidx.compose.runtime.Immutable

/** Everything the card needs, already formatted, so composition does no work beyond layout. */
@Immutable
internal data class RestaurantUiModel(
    val id: String,
    val name: String,
    val cuisines: String,
    val ratingText: String?,
    val etaMinutes: Int?,
    val deliveryFee: DeliveryFee,
    val isOpen: Boolean,
    val imageUrl: String?,
    val isFavorite: Boolean,
)
