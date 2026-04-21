package com.aarw.dexanalyze.util

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoggerTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `debug logs with correct tag`() {
        Logger.d("TestTag", "Test message")
        verify { Log.d("DEXAnalyze:TestTag", "Test message") }
    }

    @Test
    fun `info logs with correct tag`() {
        Logger.i("TestTag", "Test message")
        verify { Log.i("DEXAnalyze:TestTag", "Test message") }
    }

    @Test
    fun `warning logs with correct tag`() {
        Logger.w("TestTag", "Test warning")
        verify { Log.w("DEXAnalyze:TestTag", "Test warning", null) }
    }

    @Test
    fun `error logs with correct tag and throwable`() {
        val exception = Exception("Test error")
        Logger.e("TestTag", "Error message", exception)
        verify { Log.e("DEXAnalyze:TestTag", "Error message", exception) }
    }

    @Test
    fun `bearer token is redacted`() {
        val message = "Authorization: Bearer abc123token456"
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", "Authorization: [REDACTED]") }
    }

    @Test
    fun `accessToken field is redacted`() {
        val message = "Response: {\"accessToken\": \"secret_token_xyz\"}"
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", "Response: {\"[REDACTED]\"}") }
    }

    @Test
    fun `refreshToken field is redacted`() {
        val message = "{\"refreshToken\": \"refresh_xyz\"}"
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", "{\"[REDACTED]\"}") }
    }

    @Test
    fun `generic token field is redacted`() {
        val message = "Token data: \"token\": \"sensitive_value\""
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", "Token data: [REDACTED]") }
    }

    @Test
    fun `multiple sensitive patterns are redacted`() {
        val message = "Bearer abc123 and {\"accessToken\": \"xyz\"} and {\"token\": \"val\"}"
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", "[REDACTED] and {\"[REDACTED]\"} and [REDACTED]") }
    }

    @Test
    fun `non-sensitive messages are not modified`() {
        val message = "User logged in successfully from 192.168.1.1"
        Logger.d("TestTag", message)
        verify { Log.d("DEXAnalyze:TestTag", message) }
    }
}
