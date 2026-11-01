package com.mrd.discovery.feature.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

/** Opens a restaurant from outside the app: mrd://restaurant/{id}. The scheme is declared in the manifest. */
private const val RESTAURANT_DEEP_LINK = "mrd://restaurant"

/** The feature's only entry point. The restaurant id travels in [DetailsRoute]. */
fun NavGraphBuilder.detailsScreen(onBack: () -> Unit) {
    composable<DetailsRoute>(
        deepLinks = listOf(navDeepLink<DetailsRoute>(basePath = RESTAURANT_DEEP_LINK)),
    ) {
        DetailsScreen(onBack = onBack)
    }
}
