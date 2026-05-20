package com.example.financetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.navigation.Screen
import com.example.financetracker.ui.screens.dashboard.DashboardScreen
import com.example.financetracker.ui.screens.transactions.TransactionsScreen
import com.example.financetracker.ui.screens.insights.InsightsScreen
import com.example.financetracker.ui.screens.settings.SettingsScreen
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financetracker.data.AppDatabase
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.viewmodels.DashboardViewModel
import com.example.financetracker.ui.viewmodels.DashboardViewModelFactory
import com.example.financetracker.ui.viewmodels.TransactionsViewModel
import com.example.financetracker.ui.viewmodels.TransactionsViewModelFactory
import com.example.financetracker.utils.TransactionClassifier

/**
 * The main container for the application UI.
 * Handles the Bottom Navigation Bar and the Navigation Host for switching screens.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Insights,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                val repository = TransactionRepository(
                    database.transactionDao(), 
                    database.merchantRuleDao(), 
                    database.customCategoryDao()
                )
                val factory = DashboardViewModelFactory(repository)
                val viewModel: DashboardViewModel = viewModel(factory = factory)
                
                DashboardScreen(viewModel = viewModel)
            }

            composable(Screen.Transactions.route) {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                val repository = TransactionRepository(
                    database.transactionDao(), 
                    database.merchantRuleDao(), 
                    database.customCategoryDao()
                )
                val factory = TransactionsViewModelFactory(repository)
                val viewModel: TransactionsViewModel = viewModel(factory = factory)

                TransactionsScreen(viewModel = viewModel)
            }

            composable(Screen.Insights.route) {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                val repository = TransactionRepository(
                    database.transactionDao(), 
                    database.merchantRuleDao(), 
                    database.customCategoryDao()
                )

                InsightsScreen(repository = repository)
            }

            composable(Screen.Settings.route) {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                val repository = TransactionRepository(
                    database.transactionDao(), 
                    database.merchantRuleDao(), 
                    database.customCategoryDao()
                )
                val classifier = TransactionClassifier(repository)

                SettingsScreen(repository = repository, classifier = classifier)
            }
        }
    }
}
