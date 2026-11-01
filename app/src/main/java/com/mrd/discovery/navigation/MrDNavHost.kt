package com.mrd.discovery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mrd.discovery.feature.details.DetailsRoute
import com.mrd.discovery.feature.details.detailsScreen
import com.mrd.discovery.feature.discovery.DiscoveryRoute
import com.mrd.discovery.feature.discovery.discoveryScreen

/**
 * The one place that knows how features connect. Features report events (a restaurant was clicked,
 * back was pressed) and this graph decides where each one leads.
 */
@Composable
fun MrDNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = DiscoveryRoute,
        modifier = modifier,
    ) {
        discoveryScreen(onRestaurantClick = { id -> navController.navigate(DetailsRoute(id)) })
        detailsScreen(onBack = { navController.popBackStack() })
    }
}
