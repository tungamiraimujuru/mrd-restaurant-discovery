package com.mrd.discovery.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class RestaurantsResponse(val restaurants: List<RestaurantDto> = emptyList())
