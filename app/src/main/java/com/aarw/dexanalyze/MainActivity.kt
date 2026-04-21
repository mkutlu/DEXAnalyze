package com.aarw.dexanalyze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.aarw.dexanalyze.ui.navigation.AppNavigation
import com.aarw.dexanalyze.ui.theme.DEXAnalyzeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as DEXAnalyzeApp
            val isDarkTheme = app.userPreferences.isDarkTheme.collectAsState()
            DEXAnalyzeTheme(isDarkTheme = isDarkTheme.value) {
                AppNavigation(
                    authRepository = app.authRepository,
                    scanRepository = app.scanRepository,
                    userPreferences = app.userPreferences
                )
            }
        }
    }
}
