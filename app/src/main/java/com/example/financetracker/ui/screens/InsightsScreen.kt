package com.example.financetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.theme.*

@Composable
fun InsightsScreen(repository: TransactionRepository) {
    // 1. Collect the grouped data from the database
    val categoryTotals by repository.getSpendingByCategory().collectAsState(initial = emptyList())

    // 2. Calculate the grand total to determine pie slice percentages

    val grandTotal = categoryTotals.sumOf { it.totalAmount } //lambda expression to sum up each category's total

    // 3. A beautiful color palette for our chart slices
    val chartColors = listOf(
        Color(0xFF6C63FF), // Brand Purple
        Color(0xFF00C4B5), // Teal
        Color(0xFFFF6584), // Coral Red
        Color(0xFFFFD166), // Yellow
        Color(0xFF06D6A0), // Mint Green
        Color(0xFF118AB2)  // Deep Blue
    )

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)
    ) {
        Text("Insights & Analytics", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(24.dp))

        if (categoryTotals.isEmpty()) {
            Text("No spending data available yet.", color = TextSecondary)
            return@Column
        }

        // --- The Chart Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Spending Breakdown", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // The Custom Compose Canvas Donut Chart
                // We make the Box fill the width and give it a fixed height
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(220.dp)) {

                    // We make the Canvas slightly smaller (160.dp) so the 60f stroke has room to expand outward
                    Canvas(modifier = Modifier.size(160.dp)) {
                        var startAngle = -90f

                        categoryTotals.forEachIndexed { index, categoryTotal ->
                            val sweepAngle = ((categoryTotal.totalAmount / grandTotal) * 360f).toFloat()
                            val color = chartColors[index % chartColors.size]

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 60f, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // The center text remains perfectly in the middle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", color = TextSecondary, fontSize = 12.sp)
                        Text("₹%,.0f".format(grandTotal), fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Top Categories", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // --- The List of Categories ---
        LazyColumn {
            items(categoryTotals.size) { index ->
                val item = categoryTotals[index]
                val color = chartColors[index % chartColors.size]
                val percentage = ((item.totalAmount / grandTotal) * 100).toInt()

                CategoryRowItem(
                    categoryName = item.category,
                    amount = item.totalAmount,
                    percentage = percentage,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CategoryRowItem(categoryName: String, amount: Double, percentage: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The colored dot matching the chart slice
            Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(categoryName, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("$percentage% of total", fontSize = 12.sp, color = TextSecondary)
            }
        }
        Text("₹%,.2f".format(amount), fontWeight = FontWeight.Bold, color = ExpenseRed)
    }
}