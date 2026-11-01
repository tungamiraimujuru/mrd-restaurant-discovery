package com.mrd.discovery.core.domain.model

/** The only two filters in scope: name search and an "open now" toggle. */
data class RestaurantFilter(val query: String = "", val openNowOnly: Boolean = false) {
    /** True when the filter narrows the list, so an empty result can say "no matches". */
    val isActive: Boolean
        get() = query.isNotBlank() || openNowOnly
}
