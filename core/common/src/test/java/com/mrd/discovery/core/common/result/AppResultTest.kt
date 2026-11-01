package com.mrd.discovery.core.common.result

import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {
    @Test
    fun successWrapsValue() {
        assertEquals(AppResult.Success(42), appResultOf { 42 })
    }

    @Test
    fun ioExceptionBecomesNetworkError() {
        val result = appResultOf<Unit> { throw IOException("offline") }
        assertTrue((result as AppResult.Failure).error is AppError.Network)
    }

    @Test
    fun socketTimeoutBecomesTimeoutError() {
        val result = appResultOf<Unit> { throw SocketTimeoutException() }
        assertTrue((result as AppResult.Failure).error is AppError.Timeout)
    }

    @Test
    fun otherExceptionBecomesUnknownError() {
        val result = appResultOf<Unit> { error("boom") }
        assertTrue((result as AppResult.Failure).error is AppError.Unknown)
    }

    @Test
    fun cancellationIsRethrown() {
        assertThrows(CancellationException::class.java) {
            appResultOf<Unit> { throw CancellationException("cancelled") }
        }
    }

    @Test
    fun mapTransformsSuccessAndKeepsFailure() {
        assertEquals(AppResult.Success(4), AppResult.Success(2).map { it * 2 })
        val failure: AppResult<Int> = AppResult.Failure(AppError.Unknown())
        assertNull(failure.map { it * 2 }.getOrNull())
    }
}
