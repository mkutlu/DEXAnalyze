package com.aarw.dexanalyze.data.api

import com.aarw.dexanalyze.data.auth.TokenStore
import com.aarw.dexanalyze.util.Logger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://app.bodyspec.com/api/v1/"
    private const val TAG = "ApiClient"

    fun create(tokenStore: TokenStore, onUnauthorized: () -> Unit = {}): BodySpecApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenStore.accessToken
                val request = chain.request().newBuilder()
                    .apply { if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token") }
                    .build()

                Logger.d(TAG, "API Request: ${request.method} ${request.url}")

                val response = chain.proceed(request)
                Logger.d(TAG, "API Response: ${response.code} ${request.method} ${request.url.encodedPath}")

                if (response.code == 401) {
                    Logger.w(TAG, "Authentication failed (401) — session expired")
                    onUnauthorized()
                }
                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BodySpecApiService::class.java)
    }
}
