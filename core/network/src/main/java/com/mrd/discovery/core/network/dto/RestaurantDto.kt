package com.mrd.discovery.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wire format. Every field is optional because real data omits or nulls fields. */
@Serializable
data class RestaurantDto(
    val id: String? = null,
    val name: String? = null,
    val cuisines: List<String>? = null,
    val rating: Double? = null,
    @SerialName("delivery_fee_cents") val deliveryFeeCents: Int? = null,
    @SerialName("eta_minutes") val etaMinutes: Int? = null,
    @SerialName("is_open") val isOpen: Boolean? = null,
    @SerialName("image_url") val imageUrl: String? = null,
)
