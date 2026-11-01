package com.mrd.discovery.core.common.format

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

/**
 * The only place money is turned into text. Amounts are integer cents, formatted as
 * rand with a dot decimal and comma grouping, e.g. 150000 -> "R1,500.00".
 * Fixed symbols keep the output identical across devices and JDKs.
 */
class CurrencyFormatter @Inject constructor() {
    fun format(cents: Long): String {
        val amount = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.ROOT))
            .format(BigDecimal.valueOf(abs(cents), 2))
        val sign = if (cents < 0) "-" else ""
        return "${sign}R$amount"
    }

    fun format(cents: Int): String = format(cents.toLong())
}
