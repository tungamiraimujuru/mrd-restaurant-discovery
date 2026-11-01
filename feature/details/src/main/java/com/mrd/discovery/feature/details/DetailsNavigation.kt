package com.mrd.discovery.feature.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/** The feature's only entry point. The restaurant id travels in [DetailsRoute]. */
fun NavGraphBuilder.detailsScreen(onBack: () -> Unit) {
    composable<DetailsRoute> {
        DetailsScreen(onBack = onBack)
    }
}
