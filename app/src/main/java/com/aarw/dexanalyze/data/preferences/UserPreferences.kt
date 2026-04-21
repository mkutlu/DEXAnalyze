package com.aarw.dexanalyze.data.preferences

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _useMetric = MutableStateFlow(prefs.getBoolean(KEY_USE_METRIC, true))
    val useMetric: StateFlow<Boolean> = _useMetric.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setUseMetric(value: Boolean) {
        prefs.edit { putBoolean(KEY_USE_METRIC, value) }
        _useMetric.value = value
    }

    fun setDarkTheme(value: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, value) }
        _isDarkTheme.value = value
    }

    private companion object {
        const val KEY_USE_METRIC = "use_metric"
        const val KEY_DARK_THEME = "dark_theme"
    }
}
