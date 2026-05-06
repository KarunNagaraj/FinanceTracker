package com.example.financetracker.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.theme.*
import com.example.financetracker.utils.CsvImporter
import com.example.financetracker.utils.TransactionClassifier
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: TransactionRepository, classifier: TransactionClassifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // Check if we already have permission
    var hasSmsPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
    }

    // The Android system launcher that shows the "Allow App to read SMS?" popup
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasSmsPermission = permissions[Manifest.permission.RECEIVE_SMS] == true
        }
    ) // remember is used to retain value across recompositions. The contract is meant to tell Android what permissions we want,
    // onResult is what the user selects for permissions. "permissions" contains what the user has selected, we compare that with true, if true==true then hasSms will be true

    // NEW: The Android System File Picker for CSVs
    val csvPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                Toast.makeText(context, "Analyzing CSV...", Toast.LENGTH_SHORT).show()

                // 1. Read and categorize the file
                val transactions = CsvImporter.parseAndImport(context, uri, classifier)

                // 2. Save them all to the database
                transactions.forEach { repository.insertTransaction(it) }

                Toast.makeText(context, "Imported ${transactions.size} transactions!", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Data Import Section ---
        Text("Data Management", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable {
                // Launch the file picker, filtering for any file type (we assume user picks a .csv)
                csvPickerLauncher.launch("*/*")
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Import Historical Bank Statement", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Upload a .csv file to analyze past spending", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Permissions Section (From Phase 3) ---
        Text("Permissions", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Background SMS Sync", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = if (hasSmsPermission) "Active" else "Inactive", fontSize = 12.sp, color = if (hasSmsPermission) IncomeGreen else TextSecondary)
                }
                Switch(checked = hasSmsPermission, onCheckedChange = { if (it) permissionLauncher.launch(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)) })
            }
        }
    }
}