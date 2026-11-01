package com.mrd.discovery.core.network.fake

/** Where the mock server gets its JSON. Assets on device, a string in tests. */
internal fun interface RestaurantPayloadSource {
    fun read(): String
}
