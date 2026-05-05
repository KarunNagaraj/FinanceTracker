package com.example.financetracker.ui.screens




import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.financetracker.ui.theme.*

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    // Check if we already have permission
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    // The Android system launcher that shows the "Allow App to read SMS?" popup
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasSmsPermission = permissions[Manifest.permission.RECEIVE_SMS] == true
        }
    ) // remember is used to retain value across recompositions. The contract is meant to tell Android what permissions we want,
    // onResult is what the user selects for permissions. "permissions" contains what the user has selected, we compare that with true, if true==true then hasSms will be true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Permissions", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

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
                    Text("Background SMS Sync", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = if (hasSmsPermission) "Active: Listening for bank alerts" else "Inactive: Requires permission",
                        fontSize = 12.sp,
                        color = if (hasSmsPermission) IncomeGreen else TextSecondary
                    )
                }

                Switch(
                    checked = hasSmsPermission,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // Ask for permission!
                            permissionLauncher.launch(
                                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
                            )
                        } else {
                            // Note: You can't programmatically revoke permissions,
                            // the user has to do it in Android Settings.
                            // We just toggle the visual state for now.
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = BrandBlue, checkedTrackColor = BrandBlue.copy(alpha = 0.5f))
                )
            }
        }
    }
}