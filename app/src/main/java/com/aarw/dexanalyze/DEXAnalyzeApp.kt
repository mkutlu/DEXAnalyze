package com.aarw.dexanalyze

import android.app.Application
import com.aarw.dexanalyze.data.api.ApiClient
import com.aarw.dexanalyze.data.auth.AuthRepository
import com.aarw.dexanalyze.data.auth.TokenStore
import com.aarw.dexanalyze.data.preferences.UserPreferences
import com.aarw.dexanalyze.data.repository.ScanRepository

class DEXAnalyzeApp : Application() {

    val tokenStore: TokenStore by lazy { TokenStore(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }

    val authRepository: AuthRepository by lazy { AuthRepository(tokenStore) }

    val scanRepository: ScanRepository by lazy {
        ScanRepository(
            apiService = ApiClient.create(tokenStore, onUnauthorized = { authRepository.logout() }),
            demoMode = false
        )
    }
}
