package com.example.financetracker.ui.screens.settings.components

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
fun PermissionCard(
    hasSmsPermission: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Text(
        text = "Permissions",
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Background SMS Sync",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (hasSmsPermission) "Active" else "Inactive",
                    fontSize = 12.sp,
                    color = if (hasSmsPermission) IncomeGreen else TextSecondary
                )
            }
            Switch(
                checked = hasSmsPermission,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
