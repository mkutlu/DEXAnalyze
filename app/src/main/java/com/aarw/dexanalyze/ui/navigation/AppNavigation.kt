package com.aarw.dexanalyze.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aarw.dexanalyze.data.auth.AuthRepository
import com.aarw.dexanalyze.data.preferences.UserPreferences
import com.aarw.dexanalyze.data.repository.ScanRepository
import com.aarw.dexanalyze.ui.LocalUseMetric
import com.aarw.dexanalyze.ui.screens.analysis.AnalysisScreen
import com.aarw.dexanalyze.ui.screens.analysis.AnalysisViewModel
import com.aarw.dexanalyze.ui.screens.dashboard.DashboardScreen
import com.aarw.dexanalyze.ui.screens.dashboard.DashboardViewModel
import com.aarw.dexanalyze.ui.screens.login.LoginScreen
import com.aarw.dexanalyze.ui.screens.login.LoginViewModel
import com.aarw.dexanalyze.ui.screens.progress.ProgressScreen
import com.aarw.dexanalyze.ui.screens.progress.ProgressViewModel
import com.aarw.dexanalyze.ui.screens.settings.SettingsScreen

private data class NavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

private val navItems = listOf(
    NavItem("dashboard", Icons.Outlined.Dashboard, "Dashboard"),
    NavItem("progress",  Icons.AutoMirrored.Outlined.ShowChart,  "Progress"),
    NavItem("analysis",  Icons.Outlined.Analytics,  "Analysis"),
    NavItem("settings",  Icons.Outlined.Settings,   "Settings")
)

@Composable
fun AppNavigation(
    authRepository: AuthRepository,
    scanRepository: ScanRepository,
    userPreferences: UserPreferences
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    val useMetric  by userPreferences.useMetric.collectAsState()

    androidx.compose.runtime.CompositionLocalProvider(LocalUseMetric provides useMetric) {
        if (!isLoggedIn) {
            LoginScreen(viewModel = viewModel(factory = LoginViewModel.Factory(authRepository)))
        } else {
            MainNavigation(
                scanRepository   = scanRepository,
                userPreferences  = userPreferences,
                onLogout         = { authRepository.logout() }
            )
        }
    }
}

@Composable
private fun MainNavigation(
    scanRepository: ScanRepository,
    userPreferences: UserPreferences,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon    = { Icon(item.icon, contentDescription = item.label) },
                        label   = { Text(item.label) },
                        selected = currentDest?.hierarchy?.any { it.route == item.route } == true,
                        onClick  = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "dashboard",
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(viewModel = viewModel(factory = DashboardViewModel.Factory(scanRepository)))
            }
            composable("progress") {
                ProgressScreen(viewModel = viewModel(factory = ProgressViewModel.Factory(scanRepository)))
            }
            composable("analysis") {
                AnalysisScreen(viewModel = viewModel(factory = AnalysisViewModel.Factory(scanRepository)))
            }
            composable("settings") {
                SettingsScreen(userPreferences = userPreferences, onLogout = onLogout)
            }
        }
    }
}
