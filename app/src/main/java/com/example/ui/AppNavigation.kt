package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.AssistantScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val expenseViewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModel.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val aiViewModel: AiViewModel = viewModel(factory = AiViewModel.Factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("dashboard", "assistant", "settings")) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") { launchSingleTop = true; popUpTo("dashboard") } },
                        icon = { 
                            val scale by animateFloatAsState(if (currentRoute == "dashboard") 1.2f else 1.0f, label = "scale")
                            Icon(Icons.Default.Home, contentDescription = "Beranda", modifier = Modifier.scale(scale)) 
                        },
                        label = { Text("Beranda") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "assistant",
                        onClick = { navController.navigate("assistant") { launchSingleTop = true } },
                        icon = { 
                            val scale by animateFloatAsState(if (currentRoute == "assistant") 1.2f else 1.0f, label = "scale")
                            Icon(Icons.Default.Lightbulb, contentDescription = "Ai Assist", modifier = Modifier.scale(scale)) 
                        },
                        label = { Text("Ai Assist") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "settings",
                        onClick = { navController.navigate("settings") { launchSingleTop = true } },
                        icon = { 
                            val scale by animateFloatAsState(if (currentRoute == "settings") 1.2f else 1.0f, label = "scale")
                            Icon(Icons.Default.Person, contentDescription = "Saya", modifier = Modifier.scale(scale)) 
                        },
                        label = { Text("Saya") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "dashboard", modifier = Modifier.padding(innerPadding)) {
            composable("dashboard", enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
                DashboardScreen(navController = navController, viewModel = expenseViewModel)
            }
            composable("add", enterTransition = { slideInVertically(initialOffsetY = { it }) }, exitTransition = { slideOutVertically(targetOffsetY = { it }) }) {
                AddExpenseScreen(navController = navController, viewModel = expenseViewModel)
            }
            composable("assistant", enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
                AssistantScreen(expenseViewModel = expenseViewModel, aiViewModel = aiViewModel, settingsViewModel = settingsViewModel)
            }
            composable("settings", enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
                SettingsScreen(settingsViewModel = settingsViewModel)
            }
        }
    }
}
