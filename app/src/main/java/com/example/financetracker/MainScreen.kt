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
import com.example.financetracker.ui.screens.*

//Bottom Nav bar + logic to navigate

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Insights,
        Screen.Settings
    )
// scaffold is for the entire screen but by passing bottom bar or top bar or floating button to it, you can control the layout cleanly, the funtion body of it will alwyas have the main content
    // The inner padding is used so that the main content doesnt overlap with the bottom bar
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()// backstack entry is a Compose state now so whenever you go to a new tab, the ui rerenders because the state has changed thanks to this assignment,
                // You may ask why should we observe the backstack entry as a state to redraw the ui, shouldnt that be inbuilt? yes normally but in this case we want the bottom nav bar to reflect which tab we are on,
                // so in this case the ui for the bottom bar must be redrawn whenever we navigate, hence we have to observe the backstack entry as a state
                val currentRoute = navBackStackEntry?.destination?.route //extract the route name of the current screen

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route, //highlighting current route and also ui gets redrawn showing this tab as selected
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }//when click back, it should go to the start of the navigation, essentially rewriting the stack to always be Screen A->new screen
                                launchSingleTop = true // if user clicks a tab multiple times, still generate it only once
                                restoreState = true // when going back to a previous tab, the ui position should be saved rather than resetting it
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
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Transactions.route) { TransactionsScreen() }
            composable(Screen.Insights.route) { InsightsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}