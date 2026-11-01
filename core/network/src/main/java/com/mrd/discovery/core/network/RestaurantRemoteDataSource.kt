package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.dto.RestaurantDto

/** The only network surface other modules see. Failures are thrown as IOException subclasses. */
interface RestaurantRemoteDataSource {
    suspend fun fetchRestaurants(): List<RestaurantDto>
}
