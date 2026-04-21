package com.aarw.dexanalyze.util

import org.junit.Test

class LoggerTest {

    @Test
    fun `sanitize redacts bearer tokens`() {
        val message = "Authorization: Bearer abc123token456"
        val sanitized = Logger.sanitize(message)
        assert(!sanitized.contains("Bearer abc123token456"))
        assert(sanitized.contains("[REDACTED]"))
    }

    @Test
    fun `sanitize redacts accessToken field`() {
        val message = "Response: {\"accessToken\": \"secret_token_xyz\"}"
        val sanitized = Logger.sanitize(message)
        assert(!sanitized.contains("secret_token_xyz"))
        assert(sanitized.contains("[REDACTED]"))
    }

    @Test
    fun `sanitize redacts refreshToken field`() {
        val message = "{\"refreshToken\": \"refresh_xyz\"}"
        val sanitized = Logger.sanitize(message)
        assert(!sanitized.contains("refresh_xyz"))
        assert(sanitized.contains("[REDACTED]"))
    }

    @Test
    fun `sanitize redacts generic token field`() {
        val message = "Token data: \"token\": \"sensitive_value\""
        val sanitized = Logger.sanitize(message)
        assert(!sanitized.contains("sensitive_value"))
        assert(sanitized.contains("[REDACTED]"))
    }

    @Test
    fun `sanitize handles multiple sensitive patterns`() {
        val message = "Bearer abc123 and {\"accessToken\": \"xyz\"} and {\"token\": \"val\"}"
        val sanitized = Logger.sanitize(message)
        assert(sanitized.contains("[REDACTED]"))
        assert(!sanitized.contains("abc123"))
        assert(!sanitized.contains("xyz"))
        assert(!sanitized.contains("val"))
    }

    @Test
    fun `sanitize preserves non-sensitive messages`() {
        val message = "User logged in successfully from 192.168.1.1"
        val sanitized = Logger.sanitize(message)
        assert(sanitized == message)
    }

    @Test
    fun `empty message is handled`() {
        val sanitized = Logger.sanitize("")
        assert(sanitized.isEmpty())
    }

    @Test
    fun `message with only whitespace is preserved`() {
        val message = "   \n\t  "
        val sanitized = Logger.sanitize(message)
        assert(sanitized == message)
    }
}
