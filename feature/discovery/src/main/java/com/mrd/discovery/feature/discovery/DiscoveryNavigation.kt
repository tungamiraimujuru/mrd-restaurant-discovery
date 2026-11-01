package com.mrd.discovery.feature.discovery

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * The feature's only entry point. [onRestaurantClick] reports the id and nothing more: this module
 * does not know where the click leads, which keeps it independent of every other feature.
 */
fun NavGraphBuilder.discoveryScreen(onRestaurantClick: (restaurantId: String) -> Unit) {
    composable<DiscoveryRoute> {
        DiscoveryPlaceholderScreen(onRestaurantClick = onRestaurantClick)
    }
}
