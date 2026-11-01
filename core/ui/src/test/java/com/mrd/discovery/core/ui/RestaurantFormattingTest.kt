package com.mrd.discovery.core.ui

import com.mrd.discovery.core.common.format.CurrencyFormatter
import com.mrd.discovery.core.ui.format.DeliveryFee
import com.mrd.discovery.core.ui.format.deliveryFeeOf
import com.mrd.discovery.core.ui.format.formatCuisines
import com.mrd.discovery.core.ui.format.formatRating
import org.junit.Assert.assertEquals
import org.junit.Test

class RestaurantFormattingTest {
    private val formatter = CurrencyFormatter()

    @Test
    fun cuisinesAreJoinedWithAMiddleDot() =
        assertEquals("Steakhouse · Grill", formatCuisines(listOf("Steakhouse", "Grill")))

    @Test
    fun noCuisinesGivesAnEmptyLine() = assertEquals("", formatCuisines(emptyList()))

    @Test
    fun ratingAlwaysHasOneDecimal() {
        assertEquals("4.6", formatRating(4.6))
        assertEquals("4.0", formatRating(4.0))
    }

    @Test
    fun missingFeeIsUnknownNotFree() = assertEquals(DeliveryFee.Unknown, deliveryFeeOf(null, formatter))

    @Test
    fun zeroFeeIsFree() = assertEquals(DeliveryFee.Free, deliveryFeeOf(0, formatter))

    @Test
    fun positiveFeeIsFormattedAsMoney() = assertEquals(DeliveryFee.Paid("R15.00"), deliveryFeeOf(1500, formatter))
}
