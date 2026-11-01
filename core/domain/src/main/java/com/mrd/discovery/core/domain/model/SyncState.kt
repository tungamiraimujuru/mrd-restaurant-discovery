package com.mrd.discovery.core.domain.model

import com.mrd.discovery.core.common.result.AppError

/**
 * Progress of refreshing the cache from the network. Separate from the restaurant list so the
 * UI can show cached data together with "refresh failed" instead of choosing one.
 */
data class SyncState(
    val isRefreshing: Boolean = false,
    val lastSyncedAtMillis: Long? = null,
    val error: AppError? = null,
)
