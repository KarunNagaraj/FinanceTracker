package com.example.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.*
import com.example.financetracker.ui.viewmodels.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    // collect as state is needed to make the ui change whenever these values change
    val expenses by viewModel.totalExpenses.collectAsState()
    val income by viewModel.totalIncome.collectAsState()

    // NEW: Format the raw doubles into clean, 2-decimal currency strings
    val formattedExpenses = "₹%,.2f".format(expenses)
    val formattedIncome = "₹%,.2f".format(income)
    val formattedSavings = "₹%,.2f".format(income - expenses)

    val gradientBrush = Brush.verticalGradient(listOf(BrandBlue, BrandPurple))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
    ) {
        // Top App Bar Area
        Text("Welcome back", color = CardWhite.copy(alpha = 0.8f), fontSize = 14.sp)
        Text("Good Evening", color = CardWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Total Monthly Spending", color = TextSecondary)

                // LIVE DATA HERE - Updated with formatting
                Text(formattedExpenses, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = BrandBlue)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Remaining Budget", color = TextSecondary)
                    Text("₹12,000", fontWeight = FontWeight.Bold) // We will make this live later
                }
                LinearProgressIndicator(
                    progress = if (expenses > 0) 0.5f else 0f,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    color = BrandPurple,
                    trackColor = BackgroundLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metric Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard("Income", formattedIncome, IncomeGreen, Modifier.weight(1f))
            MetricCard("Expense", formattedExpenses, ExpenseRed, Modifier.weight(1f))
            MetricCard("Savings", formattedSavings, SavingsBlue, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Temporary Button to test the database
        Button(
            onClick = { viewModel.simulateIncomingSms() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CardWhite, contentColor = BrandBlue)
        ) {
            Text("Simulate Bank SMS")
        }
    }
}

@Composable
fun MetricCard(title: String, amount: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Text(amount, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}