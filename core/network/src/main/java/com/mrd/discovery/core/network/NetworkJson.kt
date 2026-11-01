package com.mrd.discovery.core.network

import kotlinx.serialization.json.Json

/** Lenient on purpose: new server fields must not break old app versions. */
internal fun createNetworkJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
