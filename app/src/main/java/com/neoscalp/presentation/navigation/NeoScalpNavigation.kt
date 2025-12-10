package com.neoscalp.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neoscalp.presentation.screens.dashboard.DashboardScreen
import com.neoscalp.presentation.screens.symbols.SymbolsScreen
import com.neoscalp.presentation.screens.parameters.ParametersScreen
import com.neoscalp.presentation.screens.settings.SettingsScreen
import com.neoscalp.presentation.screens.backtest.BacktestScreen
import com.neoscalp.presentation.screens.logs.LogsScreen
import com.neoscalp.presentation.theme.NeonGreen
import com.neoscalp.presentation.theme.Black

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Symbols : Screen("symbols", "Symbols", Icons.Default.Search)
    object Parameters : Screen("parameters", "Parameters", Icons.Default.Tune)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Backtest : Screen("backtest", "Backtest", Icons.Default.Timeline)
    object Logs : Screen("logs", "Logs", Icons.Default.List)
}

@Composable
fun NeoScalpNavigation() {
    val navController = rememberNavController()
    
    val items = listOf(
        Screen.Dashboard,
        Screen.Symbols,
        Screen.Parameters,
        Screen.Settings,
        Screen.Logs
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Black,
                contentColor = NeonGreen
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            unselectedIconColor = NeonGreen.copy(alpha = 0.5f),
                            unselectedTextColor = NeonGreen.copy(alpha = 0.5f),
                            indicatorColor = NeonGreen.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .padding(innerPadding)
                .background(Black)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Symbols.route) { SymbolsScreen() }
            composable(Screen.Parameters.route) { ParametersScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Backtest.route) { BacktestScreen() }
            composable(Screen.Logs.route) { LogsScreen() }
        }
    }
}
