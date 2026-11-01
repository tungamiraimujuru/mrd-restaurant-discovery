package com.mrd.discovery.core.common.logging

/**
 * The only way to log in this codebase. Never pass names, addresses, tokens or other PII.
 */
interface AppLogger {
    fun debug(tag: String, message: String)

    fun error(tag: String, message: String, throwable: Throwable? = null)
}
