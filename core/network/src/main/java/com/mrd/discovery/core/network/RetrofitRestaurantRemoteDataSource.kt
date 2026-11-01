package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.dto.RestaurantDto
import javax.inject.Inject
import retrofit2.HttpException

internal class RetrofitRestaurantRemoteDataSource @Inject constructor(private val api: RestaurantApi) :
    RestaurantRemoteDataSource {
    override suspend fun fetchRestaurants(): List<RestaurantDto> = try {
        api.getRestaurants().restaurants
    } catch (e: HttpException) {
        throw HttpFailureException(e.code(), e)
    }
}
