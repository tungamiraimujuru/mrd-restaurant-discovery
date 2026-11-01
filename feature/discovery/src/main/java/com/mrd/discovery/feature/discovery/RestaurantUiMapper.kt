package com.mrd.discovery.feature.discovery

import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.domain.model.Restaurant
import java.util.Locale

private const val CUISINE_SEPARATOR = " · "

internal fun Restaurant.toUiModel(currencyFormatter: CurrencyFormatter) = RestaurantUiModel(
    id = id,
    name = name,
    cuisines = cuisines.joinToString(CUISINE_SEPARATOR),
    ratingText = rating?.let { String.format(Locale.ROOT, "%.1f", it) },
    etaMinutes = etaMinutes,
    deliveryFee = when (val fee = deliveryFeeCents) {
        null -> DeliveryFee.Unknown
        0 -> DeliveryFee.Free
        else -> DeliveryFee.Paid(currencyFormatter.format(fee))
    },
    isOpen = isOpen,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
)
