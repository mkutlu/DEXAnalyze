package com.aarw.dexanalyze.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStore(context: Context) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "dexanalyze_auth",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        private set(value) { prefs.edit { putString(KEY_ACCESS_TOKEN, value) } }

    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)
        private set(value) { prefs.edit { putString(KEY_REFRESH_TOKEN, value) } }

    private var tokenExpiry: Long
        get() = prefs.getLong(KEY_EXPIRY, 0L)
        set(value) { prefs.edit { putLong(KEY_EXPIRY, value) } }

    fun saveTokens(accessToken: String, refreshToken: String?, expiry: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpiry = expiry
    }

    fun isLoggedIn(): Boolean = !accessToken.isNullOrBlank()

    fun isTokenExpired(): Boolean {
        val expiry = tokenExpiry
        return expiry > 0 && System.currentTimeMillis() > expiry - 60_000
    }

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRY = "token_expiry"
    }
}
