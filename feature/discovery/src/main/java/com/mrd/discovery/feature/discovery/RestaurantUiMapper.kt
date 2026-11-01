package com.mrd.discovery.feature.discovery

import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.ui.format.deliveryFeeOf
import com.mrd.discovery.core.ui.format.formatCuisines
import com.mrd.discovery.core.ui.format.formatRating

internal fun Restaurant.toUiModel(currencyFormatter: CurrencyFormatter) = RestaurantUiModel(
    id = id,
    name = name,
    cuisines = formatCuisines(cuisines),
    ratingText = rating?.let { formatRating(it) },
    etaMinutes = etaMinutes,
    deliveryFee = deliveryFeeOf(deliveryFeeCents, currencyFormatter),
    isOpen = isOpen,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
)
