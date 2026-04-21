package com.aarw.dexanalyze.data.auth

object OAuthConfig {
    const val CLIENT_ID = "bodyspec-api-ext-v1"
    const val AUTH_ENDPOINT =
        "https://auth.bodyspec.com/realms/bodyspec/protocol/openid-connect/auth"
    const val TOKEN_ENDPOINT =
        "https://auth.bodyspec.com/realms/bodyspec/protocol/openid-connect/token"
    const val REDIRECT_URI = "https://app.bodyspec.com/docs"
    val SCOPES = listOf("openid", "profile", "email")
}
