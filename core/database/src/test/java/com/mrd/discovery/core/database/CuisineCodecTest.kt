package com.mrd.discovery.core.database

import org.junit.Assert.assertEquals
import org.junit.Test

class CuisineCodecTest {
    @Test
    fun roundTripsCuisines() {
        val cuisines = listOf("Steakhouse", "Grill")
        assertEquals(cuisines, CuisineCodec.decode(CuisineCodec.encode(cuisines)))
    }

    @Test
    fun emptyListRoundTrips() = assertEquals(emptyList<String>(), CuisineCodec.decode(CuisineCodec.encode(emptyList())))

    @Test
    fun separatorInsideANameCannotSplitIt() {
        assertEquals(listOf("A B"), CuisineCodec.decode(CuisineCodec.encode(listOf("A\u001FB"))))
    }
}
