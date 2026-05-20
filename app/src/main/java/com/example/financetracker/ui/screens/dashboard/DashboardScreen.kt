package com.example.financetracker.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.financetracker.ui.screens.dashboard.components.DashboardHeader
import com.example.financetracker.ui.screens.dashboard.components.MetricCard
import com.example.financetracker.ui.screens.dashboard.components.SummaryCard
import com.example.financetracker.ui.theme.*
import com.example.financetracker.ui.viewmodels.DashboardViewModel

/**
 * Main entry point for the Dashboard.
 * Orchestrates the display of financial summaries and metrics.
 */
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    // Collect financial data from the ViewModel
    val expenses by viewModel.totalExpenses.collectAsState()
    val income by viewModel.totalIncome.collectAsState()

    // Presentation Logic: Format raw doubles into clean currency strings
    // This keeps the UI code focused on layout rather than string manipulation
    val formattedExpenses = "₹%,.2f".format(expenses)
    val formattedIncome = "₹%,.2f".format(income)
    val formattedSavings = "₹%,.2f".format(income - expenses)
    
    // Calculate progress for the spending indicator
    val expensesProgress = if (expenses > 0) 0.5f else 0f // Budget logic to be expanded later

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(BrandBlue, BrandPurple)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
    ) {
        DashboardHeader()

        SummaryCard(
            formattedExpenses = formattedExpenses,
            expensesProgress = expensesProgress
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Section: Quick glance at Income, Expense, and Savings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Income",
                amount = formattedIncome,
                color = IncomeGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Expense",
                amount = formattedExpenses,
                color = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Savings",
                amount = formattedSavings,
                color = SavingsBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Section: Simulation for testing
        Button(
            onClick = { viewModel.simulateIncomingSms() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardWhite,
                contentColor = BrandBlue
            )
        ) {
            Text("Simulate Bank SMS")
        }
    }
}
