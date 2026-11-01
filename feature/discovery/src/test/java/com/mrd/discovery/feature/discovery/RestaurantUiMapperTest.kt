package com.mrd.discovery.feature.discovery

import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.domain.model.Restaurant
import com.mrd.discovery.core.ui.format.DeliveryFee
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RestaurantUiMapperTest {
    private val formatter = CurrencyFormatter()

    private fun restaurant(
        rating: Double? = 4.6,
        fee: Int? = 1500,
        cuisines: List<String> = listOf("Steakhouse", "Grill"),
    ) = Restaurant(
        id = "r-1",
        name = "Cape Town Grill",
        cuisines = cuisines,
        rating = rating,
        deliveryFeeCents = fee,
        etaMinutes = 32,
        isOpen = true,
        imageUrl = "https://example.com/r-1.jpg",
        isFavorite = false,
    )

    @Test
    fun cuisinesAreJoinedWithAMiddleDot() =
        assertEquals("Steakhouse · Grill", restaurant().toUiModel(formatter).cuisines)

    @Test
    fun ratingIsShownWithOneDecimal() {
        assertEquals("4.6", restaurant(rating = 4.6).toUiModel(formatter).ratingText)
        assertEquals("4.0", restaurant(rating = 4.0).toUiModel(formatter).ratingText)
    }

    @Test
    fun missingRatingStaysNull() = assertNull(restaurant(rating = null).toUiModel(formatter).ratingText)

    @Test
    fun zeroFeeMeansFreeDelivery() =
        assertEquals(DeliveryFee.Free, restaurant(fee = 0).toUiModel(formatter).deliveryFee)

    @Test
    fun positiveFeeIsFormattedAsMoney() =
        assertEquals(DeliveryFee.Paid("R15.00"), restaurant(fee = 1500).toUiModel(formatter).deliveryFee)

    @Test
    fun missingFeeIsUnknownNotFree() =
        assertEquals(DeliveryFee.Unknown, restaurant(fee = null).toUiModel(formatter).deliveryFee)

    @Test
    fun otherFieldsPassThrough() {
        val ui = restaurant().toUiModel(formatter)

        assertEquals("r-1", ui.id)
        assertEquals(32, ui.etaMinutes)
        assertEquals(true, ui.isOpen)
        assertEquals("https://example.com/r-1.jpg", ui.imageUrl)
    }
}
