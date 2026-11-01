package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.dto.RestaurantsResponse
import retrofit2.http.GET

internal interface RestaurantApi {
    @GET("restaurants")
    suspend fun getRestaurants(): RestaurantsResponse
}
