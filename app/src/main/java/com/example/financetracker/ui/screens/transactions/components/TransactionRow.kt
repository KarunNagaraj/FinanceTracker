package com.example.financetracker.ui.screens.transactions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Avoid repeating raw string literals everywhere
private const val UNCATEGORIZED = "Uncategorized"

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {

    // Used to determine warning styling
    val isUncategorized =
        transaction.category == UNCATEGORIZED

    // Highlight uncategorized transactions
    val backgroundColor =
        if (isUncategorized) {
            ExpenseRed.copy(alpha = 0.1f)
        } else {
            CardWhite
        }

    val categoryColor =
        if (isUncategorized) {
            ExpenseRed
        } else {
            TextSecondary
        }

    // Credits are green, debits are red
    val amountColor =
        if (transaction.type == "CREDIT") {
            IncomeGreen
        } else {
            ExpenseRed
        }

    // Format amount for the ui rendering
    val formattedAmount =
        if (transaction.type == "CREDIT") {
            "+₹%,.2f".format(transaction.amount)
        } else {
            "-₹%,.2f".format(transaction.amount)
        }

    // Cache formatted date unless timestamp changes
    val formattedDate = remember(transaction.timestamp) {
        SimpleDateFormat(
            "dd MMM, hh:mm a",
            Locale.getDefault()
        ).format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left side = description/category/date
            TransactionInfoSection(
                transaction = transaction,
                categoryColor = categoryColor,
                formattedDate = formattedDate
            )

            // Right side = amount
            TransactionAmount(
                amount = formattedAmount,
                color = amountColor
            )
        }
    }
}

@Composable
private fun RowScope.TransactionInfoSection(
    transaction: Transaction,
    categoryColor: Color,
    formattedDate: String
) {

    Column(
        modifier = Modifier

            // Takes remaining horizontal space inside Row
            .weight(1f)

            .padding(end = 16.dp)
    ) {

        Text(
            text = transaction.rawDescription,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,

            // Prevent long names from breaking layout
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = transaction.category,
            fontSize = 12.sp,
            color = categoryColor
        )

        Text(
            text = formattedDate,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun TransactionAmount(
    amount: String,
    color: Color
) {

    @Suppress("DEPRECATION")
    Text(
        text = amount,
        fontWeight = FontWeight.Bold,
        color = color,

        // Prevent amount from wrapping to next line
        maxLines = 1
    )
}
