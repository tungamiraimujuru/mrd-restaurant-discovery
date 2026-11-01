package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.dto.RestaurantsResponse
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RestaurantPayloadTest {
    private val json = createNetworkJson()

    @Test
    fun missingAndNullFieldsParseToNull() {
        val payload = """{"restaurants":[{"id":"r-1","name":"A","rating":null}]}"""

        val dto = json.decodeFromString<RestaurantsResponse>(payload).restaurants.single()

        assertEquals("r-1", dto.id)
        assertNull(dto.rating)
        assertNull(dto.etaMinutes)
        assertNull(dto.imageUrl)
        assertNull(dto.isOpen)
    }

    @Test
    fun unknownKeysAreIgnored() {
        val payload = """{"restaurants":[{"id":"r-1","name":"A","surprise":true}],"page":2}"""

        assertEquals(1, json.decodeFromString<RestaurantsResponse>(payload).restaurants.size)
    }

    @Test
    fun snakeCaseFieldsMapToProperties() {
        val payload = """{"restaurants":[{"id":"r-1","delivery_fee_cents":1500,"eta_minutes":32,"is_open":true}]}"""

        val dto = json.decodeFromString<RestaurantsResponse>(payload).restaurants.single()

        assertEquals(1500, dto.deliveryFeeCents)
        assertEquals(32, dto.etaMinutes)
        assertEquals(true, dto.isOpen)
    }
}
