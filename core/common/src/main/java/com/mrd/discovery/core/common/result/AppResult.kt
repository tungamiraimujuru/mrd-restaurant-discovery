package com.mrd.discovery.core.common.result

import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>

    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

/**
 * Runs [block] and wraps the outcome. Cancellation is always rethrown so structured
 * concurrency keeps working. The caught exception is preserved inside the [AppError],
 * and the caller is responsible for logging it.
 */
inline fun <T> appResultOf(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: SocketTimeoutException) {
    AppResult.Failure(AppError.Timeout(e))
} catch (e: IOException) {
    AppResult.Failure(AppError.Network(e))
} catch (e: Exception) {
    AppResult.Failure(AppError.Unknown(e))
}
