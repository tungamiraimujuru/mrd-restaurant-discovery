package com.mrd.discovery.core.network

import java.io.IOException

/** A non-2xx response. An IOException so callers treat it like any other transport failure. */
class HttpFailureException(val code: Int, cause: Throwable? = null) : IOException("HTTP $code", cause)
