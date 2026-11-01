package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.fake.FakeBackendConfig
import com.mrd.discovery.core.network.fake.FakeBackendInterceptor
import com.mrd.discovery.core.network.fake.RestaurantPayloadSource
import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FakeBackendInterceptorTest {
    private val payload = """{"restaurants":[]}"""
    private val config = FakeBackendConfig().apply { latencyMillis = 0 }
    private val client = OkHttpClient.Builder()
        .addInterceptor(FakeBackendInterceptor(RestaurantPayloadSource { payload }, config))
        .build()

    private fun get(path: String) = client.newCall(Request.Builder().url("https://fake.test$path").build())

    @Test
    fun normalModeServesThePayload() {
        get("/restaurants").execute().use { response ->
            assertEquals(200, response.code)
            assertEquals(payload, response.body.string())
        }
    }

    @Test
    fun unknownPathIsNotFound() {
        get("/menus").execute().use { assertEquals(404, it.code) }
    }

    @Test
    fun offlineModeThrowsIoException() {
        config.mode = FakeBackendConfig.Mode.Offline
        assertThrows(IOException::class.java) { get("/restaurants").execute() }
    }

    @Test
    fun timeoutModeThrowsSocketTimeout() {
        config.mode = FakeBackendConfig.Mode.Timeout
        assertThrows(SocketTimeoutException::class.java) { get("/restaurants").execute() }
    }

    @Test
    fun serverErrorModeReturns503() {
        config.mode = FakeBackendConfig.Mode.ServerError
        get("/restaurants").execute().use { assertEquals(503, it.code) }
    }
}
