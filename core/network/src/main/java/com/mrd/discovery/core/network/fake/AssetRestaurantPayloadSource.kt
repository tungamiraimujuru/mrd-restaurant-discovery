package com.mrd.discovery.core.network.fake

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AssetRestaurantPayloadSource @Inject constructor(@ApplicationContext private val context: Context) :
    RestaurantPayloadSource {
    override fun read(): String = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }

    private companion object {
        const val ASSET_NAME = "restaurants.json"
    }
}
