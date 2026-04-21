package com.aarw.dexanalyze.data.auth

import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.security.MessageDigest
import java.security.SecureRandom

private interface TokenApiService {
    @FormUrlEncoded
    @POST("realms/bodyspec/protocol/openid-connect/token")
    suspend fun exchangeCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): KeycloakTokenResponse

    @FormUrlEncoded
    @POST("realms/bodyspec/protocol/openid-connect/token")
    suspend fun refresh(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): KeycloakTokenResponse
}

class AuthRepository(private val tokenStore: TokenStore) {

    private val _isLoggedIn = MutableStateFlow(tokenStore.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val tokenApi = Retrofit.Builder()
        .baseUrl("https://auth.bodyspec.com/")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TokenApiService::class.java)

    fun buildAuthUrl(): Pair<String, String> {
        val verifier = generateCodeVerifier()
        val challenge = generateCodeChallenge(verifier)
        val url = Uri.parse(OAuthConfig.AUTH_ENDPOINT).buildUpon()
            .appendQueryParameter("client_id", OAuthConfig.CLIENT_ID)
            .appendQueryParameter("redirect_uri", OAuthConfig.REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", OAuthConfig.SCOPES.joinToString(" "))
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .build()
            .toString()
        return url to verifier
    }

    suspend fun exchangeCode(code: String, codeVerifier: String): Result<Unit> =
        runCatching {
            val resp = tokenApi.exchangeCode(
                clientId = OAuthConfig.CLIENT_ID,
                code = code,
                redirectUri = OAuthConfig.REDIRECT_URI,
                codeVerifier = codeVerifier
            )
            tokenStore.saveTokens(
                accessToken = resp.accessToken,
                refreshToken = resp.refreshToken,
                expiry = System.currentTimeMillis() + resp.expiresIn * 1000L
            )
            _isLoggedIn.value = true
        }

    suspend fun refreshTokenIfNeeded(): Boolean {
        if (!tokenStore.isTokenExpired()) return true
        val refreshToken = tokenStore.refreshToken ?: return false
        return runCatching {
            val resp = tokenApi.refresh(
                clientId = OAuthConfig.CLIENT_ID,
                refreshToken = refreshToken
            )
            tokenStore.saveTokens(
                accessToken = resp.accessToken,
                refreshToken = resp.refreshToken ?: refreshToken,
                expiry = System.currentTimeMillis() + resp.expiresIn * 1000L
            )
        }.isSuccess
    }

    fun logout() {
        tokenStore.clear()
        _isLoggedIn.value = false
    }

    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun generateCodeChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
