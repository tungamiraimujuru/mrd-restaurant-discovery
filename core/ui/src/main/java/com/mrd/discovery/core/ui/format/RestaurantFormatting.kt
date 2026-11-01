package com.mrd.discovery.core.ui.format

import com.mrd.discovery.core.common.format.CurrencyFormatter
import java.util.Locale

private const val CUISINE_SEPARATOR = " · "

/** Cuisines as one line, e.g. "Steakhouse · Grill". */
fun formatCuisines(cuisines: List<String>): String = cuisines.joinToString(CUISINE_SEPARATOR)

/** Rating with exactly one decimal, e.g. "4.6" or "4.0". */
fun formatRating(rating: Double): String = String.format(Locale.ROOT, "%.1f", rating)

/** Unknown fee, free delivery (zero cents) and a paid fee must never be confused. */
fun deliveryFeeOf(cents: Int?, formatter: CurrencyFormatter): DeliveryFee = when {
    cents == null -> DeliveryFee.Unknown
    cents == 0 -> DeliveryFee.Free
    else -> DeliveryFee.Paid(formatter.format(cents))
}
