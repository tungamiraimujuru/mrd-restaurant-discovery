package com.mrd.discovery.core.domain.model

/**
 * A restaurant as the app sees it. Optional fields are null when the source omitted them,
 * so the UI decides how to present "unknown" instead of showing made-up defaults.
 */
data class Restaurant(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double?,
    val deliveryFeeCents: Int?,
    val etaMinutes: Int?,
    val isOpen: Boolean,
    val imageUrl: String?,
    val isFavorite: Boolean,
)
