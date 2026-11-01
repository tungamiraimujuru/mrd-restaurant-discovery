package com.mrd.discovery.feature.discovery

import androidx.compose.runtime.Immutable

/** Three different situations the card must tell apart: unknown, free, and a real amount. */
@Immutable
internal sealed interface DeliveryFee {
    data object Unknown : DeliveryFee

    data object Free : DeliveryFee

    data class Paid(val formatted: String) : DeliveryFee
}
