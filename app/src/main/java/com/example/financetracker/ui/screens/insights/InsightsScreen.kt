package com.example.financetracker.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.screens.insights.components.CategoryRowItem
import com.example.financetracker.ui.screens.insights.components.DonutChart
import com.example.financetracker.ui.theme.*

/**
 * Main entry point for the Insights Screen.
 * Provides a visual breakdown of spending patterns using charts and lists.
 */
@Composable
fun InsightsScreen(repository: TransactionRepository) {
    // Collect the grouped data from the database
    val categoryTotals by repository.getSpendingByCategory().collectAsState(initial = emptyList())

    // Presentation Logic: Calculate the grand total for percentages
    val grandTotal = categoryTotals.sumOf { it.totalAmount }

    // Chart color palette
    val chartColors = listOf(
        Color(0xFF6C63FF), // Brand Purple
        Color(0xFF00C4B5), // Teal
        Color(0xFFFF6584), // Coral Red
        Color(0xFFFFD166), // Yellow
        Color(0xFF06D6A0), // Mint Green
        Color(0xFF118AB2)  // Deep Blue
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        ScreenHeader()

        Spacer(modifier = Modifier.height(24.dp))

        if (categoryTotals.isEmpty()) {
            EmptyStateMessage()
            return@Column
        }

        // --- The Chart Card ---
        DonutChart(
            grandTotal = grandTotal,
            categoryTotals = categoryTotals,
            chartColors = chartColors
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Top Categories",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // --- The List of Categories ---
        CategoryBreakdownList(
            categoryTotals = categoryTotals,
            grandTotal = grandTotal,
            chartColors = chartColors
        )
    }
}

@Composable
private fun ScreenHeader() {
    Text(
        text = "Insights & Analytics",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue
    )
}

@Composable
private fun EmptyStateMessage() {
    Text(
        text = "No spending data available yet.",
        color = TextSecondary
    )
}

@Composable
private fun CategoryBreakdownList(
    categoryTotals: List<com.example.financetracker.data.dao.CategoryTotal>,
    grandTotal: Double,
    chartColors: List<Color>
) {
    LazyColumn {
        items(categoryTotals.size) { index ->
            val item = categoryTotals[index]
            val color = chartColors[index % chartColors.size]
            val percentage = if (grandTotal > 0) {
                ((item.totalAmount / grandTotal) * 100).toInt()
            } else 0

            CategoryRowItem(
                categoryName = item.category,
                amount = item.totalAmount,
                percentage = percentage,
                color = color
            )
        }
    }
}
