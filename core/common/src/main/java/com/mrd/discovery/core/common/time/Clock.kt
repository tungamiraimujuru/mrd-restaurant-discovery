package com.mrd.discovery.core.common.time

/** Injected so time-dependent code is testable. Never call System.currentTimeMillis() directly. */
fun interface Clock {
    fun nowMillis(): Long
}
