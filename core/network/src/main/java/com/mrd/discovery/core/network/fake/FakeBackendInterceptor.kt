package com.mrd.discovery.core.network.fake

import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Answers requests locally instead of calling a real server. Failures are real exceptions and
 * real HTTP error responses, so the app's error handling is exercised exactly as in production.
 */
internal class FakeBackendInterceptor(
    private val source: RestaurantPayloadSource,
    private val config: FakeBackendConfig,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        simulateLatency()
        return when (config.mode) {
            FakeBackendConfig.Mode.Offline -> throw UnknownHostException("Simulated offline")
            FakeBackendConfig.Mode.Timeout -> throw SocketTimeoutException("Simulated timeout")
            FakeBackendConfig.Mode.ServerError -> respond(request, 503, "Service Unavailable", ERROR_BODY)
            FakeBackendConfig.Mode.Normal -> route(request)
        }
    }

    private fun route(request: Request): Response = if (request.url.encodedPath == RESTAURANTS_PATH) {
        respond(request, 200, "OK", source.read())
    } else {
        respond(request, 404, "Not Found", NOT_FOUND_BODY)
    }

    private fun simulateLatency() {
        val delay = config.latencyMillis
        if (delay <= 0) return
        try {
            Thread.sleep(delay)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException("Simulated latency interrupted").apply { initCause(e) }
        }
    }

    private fun respond(request: Request, code: Int, message: String, body: String): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message(message)
        .body(body.toResponseBody(JSON.toMediaType()))
        .build()

    private companion object {
        const val JSON = "application/json"
        const val RESTAURANTS_PATH = "/restaurants"
        const val ERROR_BODY = """{"error":"service_unavailable","message":"Please try again shortly."}"""
        const val NOT_FOUND_BODY = """{"error":"not_found"}"""
    }
}
