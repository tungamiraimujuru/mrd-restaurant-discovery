package com.mrd.discovery.feature.details

import androidx.compose.runtime.Immutable
import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.ui.format.DeliveryFee
import com.mrd.discovery.core.ui.format.deliveryFeeOf
import com.mrd.discovery.core.ui.format.formatCuisines
import com.mrd.discovery.core.ui.format.formatRating

@Immutable
internal data class RestaurantDetailsUi(
    val name: String,
    val cuisines: String,
    val ratingText: String?,
    val etaMinutes: Int?,
    val deliveryFee: DeliveryFee,
    val isOpen: Boolean,
    val imageUrl: String?,
    val isFavorite: Boolean,
)

internal fun Restaurant.toDetailsUi(currencyFormatter: CurrencyFormatter) = RestaurantDetailsUi(
    name = name,
    cuisines = formatCuisines(cuisines),
    ratingText = rating?.let { formatRating(it) },
    etaMinutes = etaMinutes,
    deliveryFee = deliveryFeeOf(deliveryFeeCents, currencyFormatter),
    isOpen = isOpen,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
)
