package com.aarw.dexanalyze.util

import android.util.Log

object Logger {
    private const val TAG = "DEXAnalyze"
    private val SENSITIVE_PATTERNS = listOf(
        "Bearer\\s+[\\w-]+".toRegex(),
        "\"accessToken\"\\s*:\\s*\"[^\"]+\"".toRegex(),
        "\"refreshToken\"\\s*:\\s*\"[^\"]+\"".toRegex(),
        "\"token\"\\s*:\\s*\"[^\"]+\"".toRegex(),
    )

    fun d(tag: String, message: String) {
        Log.d("$TAG:$tag", sanitize(message))
    }

    fun i(tag: String, message: String) {
        Log.i("$TAG:$tag", sanitize(message))
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w("$TAG:$tag", sanitize(message), throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("$TAG:$tag", sanitize(message), throwable)
    }

    private fun sanitize(message: String): String {
        var sanitized = message
        SENSITIVE_PATTERNS.forEach { pattern ->
            sanitized = sanitized.replace(pattern, "[REDACTED]")
        }
        return sanitized
    }
}
