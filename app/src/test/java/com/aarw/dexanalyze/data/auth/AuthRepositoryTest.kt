package com.aarw.dexanalyze.data.auth

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `OAuthConfig has correct endpoint URLs`() {
        assertEquals("bodyspec-api-ext-v1", OAuthConfig.CLIENT_ID)
        assertTrue(OAuthConfig.AUTH_ENDPOINT.contains("auth.bodyspec.com"))
        assertTrue(OAuthConfig.TOKEN_ENDPOINT.contains("auth.bodyspec.com"))
        assertTrue(OAuthConfig.REDIRECT_URI.contains("bodyspec.com"))
    }

    @Test
    fun `OAuthConfig scopes are properly configured`() {
        val scopes = OAuthConfig.SCOPES
        assertTrue(scopes.contains("openid"))
        assertTrue(scopes.contains("profile"))
        assertTrue(scopes.contains("email"))
        assertEquals(3, scopes.size)
    }

    @Test
    fun `OAuth endpoints use HTTPS`() {
        assertTrue(OAuthConfig.AUTH_ENDPOINT.startsWith("https://"))
        assertTrue(OAuthConfig.TOKEN_ENDPOINT.startsWith("https://"))
        assertTrue(OAuthConfig.REDIRECT_URI.startsWith("https://"))
    }

    @Test
    fun `CLIENT_ID is not empty`() {
        assertFalse(OAuthConfig.CLIENT_ID.isEmpty())
        assertTrue(OAuthConfig.CLIENT_ID.contains("bodyspec"))
    }

    @Test
    fun `token endpoint contains openid-connect path`() {
        assertTrue(OAuthConfig.TOKEN_ENDPOINT.contains("openid-connect"))
        assertTrue(OAuthConfig.TOKEN_ENDPOINT.contains("token"))
    }

    @Test
    fun `auth endpoint contains correct realm`() {
        assertTrue(OAuthConfig.AUTH_ENDPOINT.contains("bodyspec"))
        assertTrue(OAuthConfig.AUTH_ENDPOINT.contains("realms"))
    }
}
