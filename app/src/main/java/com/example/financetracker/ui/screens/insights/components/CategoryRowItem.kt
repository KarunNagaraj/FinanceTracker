package com.example.financetracker.ui.screens.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.ExpenseRed
import com.example.financetracker.ui.theme.TextPrimary
import com.example.financetracker.ui.theme.TextSecondary

/**
 * Renders a single row in the category breakdown list.
 * Includes a colored dot, category name, percentage, and total amount.
 */
@Composable
fun CategoryRowItem(
    categoryName: String,
    amount: Double,
    percentage: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The colored dot matching the chart slice
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = categoryName,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$percentage% of total",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
        Text(
            text = "₹%,.2f".format(amount),
            fontWeight = FontWeight.Bold,
            color = ExpenseRed
        )
    }
}
