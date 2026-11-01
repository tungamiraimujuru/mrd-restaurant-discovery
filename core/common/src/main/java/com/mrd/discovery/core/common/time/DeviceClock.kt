package com.mrd.discovery.core.common.time

import javax.inject.Inject

internal class DeviceClock @Inject constructor() : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
