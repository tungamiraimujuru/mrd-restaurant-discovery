package com.mrd.discovery.feature.discovery

/** Sample data for previews only. */
internal fun previewRestaurant(
    id: String = "r-1001",
    name: String = "Cape Town Grill",
    isOpen: Boolean = true,
    fee: DeliveryFee = DeliveryFee.Paid("R15.00"),
) = RestaurantUiModel(
    id = id,
    name = name,
    cuisines = "Steakhouse · Grill",
    ratingText = "4.6",
    etaMinutes = 32,
    deliveryFee = fee,
    isOpen = isOpen,
    imageUrl = null,
    isFavorite = false,
)
