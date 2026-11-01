package com.mrd.discovery.core.common.result

/** Failures that cross layer boundaries. Repositories return these instead of throwing. */
sealed interface AppError {
    val cause: Throwable?

    data class Network(override val cause: Throwable? = null) : AppError

    data class Timeout(override val cause: Throwable? = null) : AppError

    data class Unknown(override val cause: Throwable? = null) : AppError
}
