package com.mrd.discovery.feature.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

/** The feature's only entry point. The restaurant id travels in [DetailsRoute]. */
fun NavGraphBuilder.detailsScreen(onBack: () -> Unit) {
    composable<DetailsRoute> { entry ->
        DetailsPlaceholderScreen(
            restaurantId = entry.toRoute<DetailsRoute>().id,
            onBack = onBack,
        )
    }
}
