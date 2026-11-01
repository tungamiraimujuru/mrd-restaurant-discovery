package com.mrd.discovery.core.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantFilterTest {
    @Test
    fun defaultFilterIsNotActive() = assertFalse(RestaurantFilter().isActive)

    @Test
    fun blankQueryIsNotActive() = assertFalse(RestaurantFilter(query = "   ").isActive)

    @Test
    fun queryActivatesFilter() = assertTrue(RestaurantFilter(query = "sushi").isActive)

    @Test
    fun openNowActivatesFilter() = assertTrue(RestaurantFilter(openNowOnly = true).isActive)
}
