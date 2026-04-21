package com.aarw.dexanalyze.data.api

import com.aarw.dexanalyze.data.auth.TokenStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://app.bodyspec.com/api/v1/"

    fun create(tokenStore: TokenStore, onUnauthorized: () -> Unit = {}): BodySpecApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenStore.accessToken
                val request = chain.request().newBuilder()
                    .apply { if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token") }
                    .build()
                val response = chain.proceed(request)
                if (response.code == 401) {
                    Log.w(TAG, "401 Unauthorized — clearing session")
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
