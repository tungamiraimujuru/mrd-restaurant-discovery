package com.mrd.discovery.core.ui.format

import androidx.compose.runtime.Immutable

/** Three different situations a screen must tell apart: unknown, free, and a real amount. */
@Immutable
sealed interface DeliveryFee {
    data object Unknown : DeliveryFee

    data object Free : DeliveryFee

    data class Paid(val formatted: String) : DeliveryFee
}
