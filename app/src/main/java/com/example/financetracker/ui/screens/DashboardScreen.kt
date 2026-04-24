package com.example.financetracker.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.*

@Composable
fun DashboardScreen() {
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
                Text("₹38,000", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = BrandBlue)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Remaining Budget", color = TextSecondary)
                    Text("₹12,000", fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = 0.75f,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    color = BrandPurple,
                    trackColor = BackgroundLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metric Row (Income, Expense, Savings Placeholders)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard("Income", "₹85k", IncomeGreen, Modifier.weight(1f))
            MetricCard("Expense", "₹38k", ExpenseRed, Modifier.weight(1f))
            MetricCard("Savings", "₹47k", SavingsBlue, Modifier.weight(1f))
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