package com.example.financetracker.ui.screens.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.CardWhite

@Composable
fun DashboardHeader() {
    Column {
        Text(
            text = "Welcome back",
            color = CardWhite.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Text(
            text = "Good Evening",
            color = CardWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
