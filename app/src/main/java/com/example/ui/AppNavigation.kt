package com.example.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.AddExpenseScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModel.Factory)

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(navController = navController, viewModel = viewModel)
        }
        composable("add") {
            AddExpenseScreen(navController = navController, viewModel = viewModel)
        }
    }
}
