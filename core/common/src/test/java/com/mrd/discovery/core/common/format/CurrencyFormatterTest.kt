package com.mrd.discovery.core.common.format

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {
    private val formatter = CurrencyFormatter()

    @Test
    fun formatsZero() = assertEquals("R0.00", formatter.format(0))

    @Test
    fun formatsWholeRand() = assertEquals("R15.00", formatter.format(1500))

    @Test
    fun formatsMenuPrice() = assertEquals("R69.00", formatter.format(6900))

    @Test
    fun padsSingleDigitCents() = assertEquals("R0.05", formatter.format(5))

    @Test
    fun groupsThousands() = assertEquals("R1,500.00", formatter.format(150_000))

    @Test
    fun formatsNegativeAmounts() = assertEquals("-R2.50", formatter.format(-250))
}
