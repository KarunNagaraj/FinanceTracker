package com.example.financetracker.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.*

@Composable
fun SummaryCard(
    formattedExpenses: String,
    expensesProgress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Total Monthly Spending",
                color = TextSecondary
            )

            // LIVE DATA HERE - Updated with formatting
            Text(
                text = formattedExpenses,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Remaining Budget", color = TextSecondary)
                Text(text = "₹12,000", fontWeight = FontWeight.Bold) // We will make this live later
            }

            LinearProgressIndicator(
                progress = { expensesProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = BrandPurple,
                trackColor = BackgroundLight
            )
        }
    }
}
