package com.example.financetracker.ui.screens.settings.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.BrandBlue

@Composable
fun SettingsHeader() {
    Text(
        text = "Settings",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue
    )
    Spacer(modifier = Modifier.height(24.dp))
}
