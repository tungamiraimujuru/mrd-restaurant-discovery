package com.mrd.discovery.core.data

import com.mrd.discovery.core.data.mapper.toCachedOrNull
import com.mrd.discovery.core.network.dto.RestaurantDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class RestaurantMappersTest {
    private fun dto(id: String? = "r-1", name: String? = "Place") = RestaurantDto(id = id, name = name)

    @Test
    fun recordWithoutIdIsDropped() = assertNull(dto(id = null).toCachedOrNull())

    @Test
    fun recordWithBlankIdIsDropped() = assertNull(dto(id = "  ").toCachedOrNull())

    @Test
    fun recordWithoutNameIsDropped() = assertNull(dto(name = null).toCachedOrNull())

    @Test
    fun textFieldsAreTrimmedAndBlankCuisinesRemoved() {
        val cached = requireNotNull(
            RestaurantDto(
                id = " r-1 ",
                name = " Cape Town Grill ",
                cuisines = listOf(" Grill ", "", "  "),
            ).toCachedOrNull(),
        )

        assertEquals("r-1", cached.id)
        assertEquals("Cape Town Grill", cached.name)
        assertEquals(listOf("Grill"), cached.cuisines)
    }

    @Test
    fun missingOptionalFieldsBecomeNullOrSafeDefaults() {
        val cached = requireNotNull(dto().toCachedOrNull())

        assertEquals(emptyList<String>(), cached.cuisines)
        assertNull(cached.rating)
        assertNull(cached.deliveryFeeCents)
        assertNull(cached.etaMinutes)
        assertNull(cached.imageUrl)
        assertFalse(cached.isOpen)
    }

    @Test
    fun outOfRangeRatingsAreDiscarded() {
        listOf(7.5, -1.0, Double.NaN).forEach { bad ->
            assertNull(RestaurantDto(id = "1", name = "A", rating = bad).toCachedOrNull()?.rating)
        }
        assertEquals(4.6, RestaurantDto(id = "1", name = "A", rating = 4.6).toCachedOrNull()?.rating)
    }

    @Test
    fun negativeFeeAndNonPositiveEtaAreDiscarded() {
        val cached = requireNotNull(
            RestaurantDto(id = "1", name = "A", deliveryFeeCents = -5, etaMinutes = 0).toCachedOrNull(),
        )

        assertNull(cached.deliveryFeeCents)
        assertNull(cached.etaMinutes)
    }

    @Test
    fun zeroFeeIsKeptBecauseItMeansFreeDelivery() {
        val cached = requireNotNull(RestaurantDto(id = "1", name = "A", deliveryFeeCents = 0).toCachedOrNull())

        assertEquals(0, cached.deliveryFeeCents)
    }

    @Test
    fun blankImageUrlBecomesNull() {
        assertNull(RestaurantDto(id = "1", name = "A", imageUrl = "   ").toCachedOrNull()?.imageUrl)
    }
}
