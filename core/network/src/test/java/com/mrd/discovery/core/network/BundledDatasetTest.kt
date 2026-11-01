package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.dto.RestaurantsResponse
import java.io.File
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guards the committed dataset: regenerate with tools/generate_restaurants.py if this fails. */
class BundledDatasetTest {
    private val all = createNetworkJson()
        .decodeFromString<RestaurantsResponse>(File("src/main/assets/restaurants.json").readText())
        .restaurants
    private val valid = all.filter { !it.id.isNullOrBlank() && !it.name.isNullOrBlank() }

    @Test
    fun datasetIsRealisticallySized() = assertTrue(valid.size >= 200)

    @Test
    fun validRecordsHaveUniqueIds() = assertEquals(valid.size, valid.map { it.id }.toSet().size)

    @Test
    fun containsTheBriefsExampleRestaurants() {
        assertTrue(valid.any { it.id == "r-1001" && it.name == "Cape Town Grill" })
        assertTrue(valid.any { it.id == "r-1002" && it.isOpen == false })
    }

    @Test
    fun exercisesMissingFieldsAndJunkRecords() {
        assertTrue(valid.any { it.rating == null })
        assertTrue(valid.any { it.imageUrl == null })
        assertTrue(all.size > valid.size)
    }
}
