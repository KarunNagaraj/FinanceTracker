package com.example.financetracker.ui.screens.insights.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.dao.CategoryTotal
import com.example.financetracker.ui.theme.*

/**
 * A custom donut chart component to visualize spending proportions.
 * @param grandTotal The sum of all spending amounts.
 * @param categoryTotals List of category names and their total amounts.
 * @param chartColors Color palette for the chart slices.
 */
@Composable
fun DonutChart(
    grandTotal: Double,
    categoryTotals: List<CategoryTotal>,
    chartColors: List<Color>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Spending Breakdown",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
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

                // Center text showing the grand total
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Total", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "₹%,.0f".format(grandTotal),
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
